package net.chilicat.testenv.webdriver;

import net.chilicat.testenv.core.*;
import net.chilicat.testenv.core.resource.Resource;
import net.chilicat.testenv.core.resource.ResourceCollector;
import net.chilicat.testenv.core.resource.Resources;
import net.chilicat.testenv.coverage.Coverage;
import net.chilicat.testenv.js.JsMessageBusInit;
import net.chilicat.testenv.server.HttpServer;
import net.chilicat.testenv.server.ScriptInit;
import net.chilicat.testenv.utils.AbstractMessageBus;
import net.chilicat.testenv.utils.MessageBus;
import net.chilicat.testenv.utils.MessageBusFactory;
import org.antlr.stringtemplate.StringTemplate;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public abstract class AbstractWebDriverTestExecutor implements TestExecutor {
    private StringTemplate siteTemplate;
    private StringTemplate scriptTemplate;
    private WebDriver driver;
    private List<String> resources = new ArrayList<String>();
    private String libs;
    private DefaultTestServer jschilicatServer;
    private Bus blockingBus;
    private Coverage coverage = Coverage.nullCoverage();
    private MessageBus messagebus;

    public void setup(final TestConfig config, final MessageBus bus) {
        if (config == null) {
            throw new NullPointerException("config");
        }

        if (bus == null) {
            throw new NullPointerException("messageBus");
        }

        this.messagebus = bus;

        try {

            siteTemplate = new StringTemplate(Utils.readAsText(getClass().getResourceAsStream("/html/TestPage.html")));
            scriptTemplate = new StringTemplate(Utils.readAsText(getClass().getResourceAsStream("/html/script.html")));

            StringBuilder buf = new StringBuilder();

            config.getServer().attachResource("/test-files/jquery.js", "/lib/jquery.js");
            scriptTemplate.reset();
            scriptTemplate.setAttribute("src", "/test-files/jquery.js");
            buf.append(scriptTemplate.toString());
            
            config.getServer().attachResource("/test-files/jschilicat-external.js", "/lib/jschilicat-external.js");
            scriptTemplate.reset();
            scriptTemplate.setAttribute("src", "/test-files/jschilicat-external.js");
            buf.append(scriptTemplate.toString());

            config.getServer().attachResource("/test-files/" + config.getFramework().toString(), config.getFramework().getFramework());
            scriptTemplate.reset();
            scriptTemplate.setAttribute("src", "/test-files/" + config.getFramework().toString());
            buf.append(scriptTemplate.toString());

            config.getServer().attachResource("/test-files/" + config.getFramework().toString() + "_adapter.js", config.getFramework().getAdapter());
            scriptTemplate.reset();
            scriptTemplate.setAttribute("src", "/test-files/" + config.getFramework().toString() + "_adapter.js");
            buf.append(scriptTemplate.toString());

            makeAvailable(config, buf, config.getLibraries());
            makeAvailable(config, buf, collect(config.getSources()));

            libs = buf.toString();

            blockingBus = new Bus();


            jschilicatServer = new DefaultTestServer(config.getServer(), "/lib/jschilicat-server.js", Collections.<String, String>emptyMap());
            jschilicatServer.init(new ScriptInit() {
                public void init(Context cx, ScriptableObject scope) {
                    final MessageBus messageBus = MessageBusFactory.composite(bus, blockingBus);
                    JsMessageBusInit.createInScope(cx, scope, messageBus);
                }
            });

            jschilicatServer.start();

            driver = createDriver();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        driver.close();
                    } catch (Throwable e) {
                        Logger.getAnonymousLogger().log(Level.FINE, "", e);

                    }
                }
            });

        } catch (IOException e) {
            throw new SetupFailedException(e);
        }
    }

    private List<File> collect(List<File> files) {
        List<File> jsFiles = new ArrayList<File>();
        final FileFilter fileFilter = Factory.jsFilter();

        for (File file : files) {
            if (file.isDirectory()) {
                ResourceCollector col = Resources.createRecursiveDirectoryCollector(file, fileFilter);
                for (Resource r : col.collectResources()) {
                    jsFiles.add(r.getFile());
                }
            } else if (fileFilter.accept(file)) {
                jsFiles.add(file);
            }
        }
        return jsFiles;
    }

    private void makeAvailable(TestConfig config, StringBuilder buf, List<File> files) {
        for (File f : files) {
            scriptTemplate.reset();
            final String path = makeAvailable(config.getServer(), f);
            scriptTemplate.setAttribute("src", path);
            resources.add(path);
            buf.append(scriptTemplate.toString());
        }
    }

    protected abstract WebDriver createDriver();

    private String makeAvailable(HttpServer server, File file) {
        String path = "/test-files/" + file.getName();
        server.attachFile(path, file);
        return path;
    }

    public void execute(TestConfig config, TestSuit suit) {
        messagebus.testSuitStart(suit.getPackage());
        try {

            for (File file : suit.getTestCases()) {
                messagebus.testScriptStart(file.getCanonicalPath());

                String path = makeAvailable(config.getServer(), file);
                scriptTemplate.reset();
                scriptTemplate.setAttribute("src", path);

                siteTemplate.reset();
                siteTemplate.setAttribute("title", file.getName());
                siteTemplate.setAttribute("env_scripts", "");
                siteTemplate.setAttribute("lib_scripts", "");
                siteTemplate.setAttribute("scr_scripts", libs);
                siteTemplate.setAttribute("test_scripts", scriptTemplate.toString());

                config.getServer().attachStringContent("/" + file.getName() + ".html", siteTemplate.toString());

                String location = "http://localhost:" + config.getServer().getPort() + "/" + file.getName() + ".html";

                Logger.getAnonymousLogger().info(siteTemplate.toString());
                Logger.getAnonymousLogger().info(String.format("\n********************\n%s\n*******************", location));

                driver.get(location);


                blockingBus.await();
                messagebus.testScriptDone(file.getCanonicalPath());
            }
        } catch (IOException e) {
            throw new TestEnvException(e);
        } finally {
            messagebus.testSuitDone(suit.getPackage());
            suit.getTestServer().stop();
        }
    }

    public void dispose(TestConfig config) {
        driver.close();
        // jschilicatServer.stop();
    }

    public void setCoverage(Coverage coverage) {
        this.coverage = coverage;
    }

    public Coverage getCoverage() {
        return coverage;
    }

    class Bus extends AbstractMessageBus {
        volatile int count = 0;
        volatile boolean started = false;
        private final Object lock = new Object();

        volatile long lastActivity;


        @Override
        public void print(String message) {
            lastActivity = System.currentTimeMillis();
        }


        @Override
        public synchronized void moduleAdded(String moduleName) {
            synchronized (lock) {
                lastActivity = System.currentTimeMillis();

                count++;
                started = true;
            }
        }

        @Override
        public void moduleDone(String moduleName) {
            synchronized (lock) {
                lastActivity = System.currentTimeMillis();

                count--;
            }
        }


        public void await() {

            long timeout = 30000;

            try {
                timeout = Long.parseLong(System.getProperty("testTimeout", "30000"));
            } catch (NumberFormatException w) {
                Logger.getAnonymousLogger().warning("System property 'testTimeout' wrong format: " + System.getProperty("testTimeout"));
            }

            lastActivity = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()) {
                synchronized (lock) {
                    long now = System.currentTimeMillis();
                    if (now - lastActivity > timeout) {
                        throw new TestEnvException("Timeout exception");
                    }

                    if (!started || count > 0) {
                        try {
                            lock.wait(10);

                            if (started && count == 0) {
                                return;
                            }
                        } catch (InterruptedException e) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }

    }
}
