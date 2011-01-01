package net.chilicat.testenv.rhino;

import net.chilicat.testenv.core.*;
import net.chilicat.testenv.core.resource.Resource;
import net.chilicat.testenv.core.resource.ResourceCollector;
import net.chilicat.testenv.core.resource.Resources;
import net.chilicat.testenv.coverage.Coverage;
import net.chilicat.testenv.js.JsMessageBusInit;
import net.chilicat.testenv.util.MessageBus;
import org.mozilla.javascript.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 */
public class RhinoTestExecutor implements TestExecutor {
    private Context context;
    private ScriptableObject scope;
    private String plainPageLocation;

    private Coverage coverage = Coverage.nullCoverage();

    private String PLAIN_PAGE_RESOURCE = "/html/PlainPage.html";
    private String INDEX = "rhino_index.html";

    private TestServer testServer;
    private MessageBus messageBus;

    public RhinoTestExecutor() {
    }

    public void setup(final TestConfig config, final MessageBus bus) {
        try {
            this.messageBus = bus;
            context = Context.enter();

            if (coverage.isEnabled()) {
                context.setDebugger(new TestEnvDebugger(coverage), null);
            }


            context.setErrorReporter(new ErrorReporter() {
                public void warning(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber) {
                    bus.log(String.format("Warning: %s %s %s %s %s ", detail, sourceName, lineNumber, lineSource, columnNumber));
                }

                public void error(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber) {
                    bus.log(String.format("Error: %s %s %s %s %s ", detail, sourceName, lineNumber, lineSource, columnNumber));
                }

                public EvaluatorException runtimeError(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber) {
                    return new EvaluatorException(detail, sourceName, lineNumber, lineSource, columnNumber);
                }
            });


            context.setLanguageVersion(Context.VERSION_1_7);
            context.setOptimizationLevel(-1);
            // cx.setDebugger(new MyDebugger(), null);

            scope = context.initStandardObjects();

            scope.defineFunctionProperties(Defaults.getNames(), Defaults.class, ScriptableObject.DONTENUM);

            JsMessageBusInit.createInScope(context, scope, messageBus);

            loadRuntime(context, scope, config.getFramework());

            loadLibs(config, scope);

            loadSources(config, scope);

            config.getServer().attachResource("/" + INDEX, PLAIN_PAGE_RESOURCE);
            plainPageLocation = String.format("http://localhost:%s/%s", config.getServer().getPort(), INDEX);

            final Map<String, String> contextMap = Collections.singletonMap("baseDir", config.getWorkingDirectory().getAbsolutePath());
            testServer = config.getServerFile() != null ? new DefaultTestServer(config.getServer(), config.getServerFile(), contextMap) : Factory.nullServer();
            testServer.start();

        } catch (IOException e) {
            throw new SetupFailedException(e);
        }
    }

    public void dispose(TestConfig config) {
        testServer.stop();

        config.getServer().detach(PLAIN_PAGE_RESOURCE);
        scope = null;
        context = null;
        Context.exit();
    }

    public void execute(TestConfig config, TestSuit suit) {
        try {
            // Create a new scope that will force that
            // each test will run in a fresh environment.
            ScriptableObject newScope = newScope();

            suit.getTestServer().start();

            try {
                if (isQUnit(config)) {
                    messageBus.log("Init QUnit");
                    context.evaluateString(newScope, "QUnit.init();", "Reinit qunit", 1, null);
                }

                messageBus.testSuitStart(suit.getPackage());

                for (File file : suit.getTestCases()) {
                    messageBus.testScriptStart(file.getCanonicalPath());

                    try {
                        messageBus.log("Load Test Script: " + file);
                        loadScript(context, newScope, file);
                    } finally {
                        messageBus.testScriptDone(file.getName());
                    }
                }

                messageBus.log("Load HTML Document: " + plainPageLocation);
                context.evaluateString(newScope, "window.location =\"" + plainPageLocation + "\";", "Load: " + plainPageLocation, 1, null);

            } finally {
                messageBus.testSuitDone(suit.getPackage());
                suit.getTestServer().stop();
            }
        } catch (IOException e) {
            throw new TestEnvException(e);
        }
    }

    private ScriptableObject newScope() {
        Scriptable newScope = context.newObject(scope);
        newScope.setPrototype(scope);
        newScope.setParentScope(null);
        return (ScriptableObject) newScope;
    }

    private boolean isQUnit(TestConfig config) {
        return config.getFramework() == TestUnitFramework.qunit;
    }

    private void loadRuntime(Context cx, ScriptableObject env, TestUnitFramework fw) throws IOException {
        String[] libs = {
                //        "/lib/env.js",
                fw.getFramework(),
                fw.getAdapter()
        };

        loadScriptsTest(cx, env, "/lib/env.js");
        loadScriptsTest(cx, env, "/lib/env-driver.js");
        loadScriptsTest(cx, env, libs);
    }

    private void loadScriptsTest(Context cx, ScriptableObject env, String... libs) throws IOException {
        for (String lib : libs) {
            InputStream in = getClass().getResourceAsStream(lib);
            if (in == null) {
                throw new SetupFailedException("Required library is not available: " + lib);
            }

            InputStreamReader reader = new InputStreamReader(in);
            Script s = compileScript(cx, reader, "<jar-resource>" + lib);
            executeScript(cx, env, s);
        }
    }

    private void loadLibs(TestConfig config, ScriptableObject env) throws IOException {
        FileFilter fileFilter = Factory.jsFilter();
        for (File file : config.getLibraries()) {
            if (file.isFile()) {
                if (fileFilter.accept(file)) {
                    messageBus.log("Load Libs: " + file);
                    loadScript(context, env, file);
                }
            } else {
                for (File child : Factory.recursiveFileCollector(file, fileFilter)) {
                    messageBus.log("Load Libs: " + child);
                    loadScript(context, env, child);
                }
            }
        }
    }

    private void loadSources(TestConfig config, ScriptableObject env) throws IOException {
        FileFilter fileFilter = Factory.jsFilter();

        final List<Resource> resources = new ArrayList<Resource>();

        for (File file : config.getSources()) {
            ResourceCollector col = Resources.createRecursiveDirectoryCollector(file, fileFilter);
            resources.addAll(col.collectResources());
        }

        for (Resource resource : resources) {
            messageBus.log("Load Sources: " + resource.getFile());
            Script s = compileScript(context, resource.getFile());

            coverage.enableCoverageFor(Context.getDebuggableView(s), resource);
            executeScript(context, env, s);
        }
    }

    private static Script loadScript(Context cx, ScriptableObject env, File a) throws IOException {
        Script script = compileScript(cx, a);
        return executeScript(cx, env, script);
    }

    private static Script executeScript(Context cx, ScriptableObject env, Script script) {
        script.exec(cx, env);
        return script;
    }

    public static Script compileScript(Context cx, File a) throws IOException {
        return compileScript(cx, new FileReader(a), a.getAbsolutePath());
    }

    public static Script compileScript(Context cx, Reader reader, String desc) throws IOException {
        long start = System.currentTimeMillis();
        try {
            Script script = cx.compileReader(reader, desc, 1, null);
            Logger.getAnonymousLogger().info("Compile Script: " + desc + " Time: " + (System.currentTimeMillis() - start) + " ms");
            return script;
        } finally {
            reader.close();
        }
    }

    public void setCoverage(Coverage coverage) {
        if (coverage == null) {
            throw new NullPointerException("coverage");
        }
        this.coverage = coverage;
    }

    public Coverage getCoverage() {
        return coverage;
    }
}
