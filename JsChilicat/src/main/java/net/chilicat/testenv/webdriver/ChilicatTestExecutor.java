package net.chilicat.testenv.webdriver;

import net.chilicat.testenv.core.SetupFailedException;
import net.chilicat.testenv.core.TestConfig;
import net.chilicat.testenv.js.JsMessageBusInit;
import net.chilicat.testenv.rhino.Defaults;
import net.chilicat.testenv.rhino.TestEnvDebugger;
import net.chilicat.testenv.utils.AbstractMessageBus;
import net.chilicat.testenv.utils.MessageBus;
import org.mozilla.javascript.*;

import java.io.*;
import java.util.logging.Logger;

/**
 * Copyright (c) 2010 <chilicat>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p/>
 * User: Chilicat
 * Date: 13.11.2010
 * Time: 15:25:53
 */
public class ChilicatTestExecutor extends AbstractWebDriverTestExecutor {

    private ScriptableObject scope;

    @Override
    public void setup(final TestConfig config, final MessageBus bus) {
        try {
            Context context = Context.enter();
            if (getCoverage().isEnabled()) {
                context.setDebugger(new TestEnvDebugger(getCoverage()), null);
            }

            context.setLanguageVersion(Context.VERSION_1_7);
            context.setOptimizationLevel(-1);
            // cx.setDebugger(new MyDebugger(), null);

            scope = context.initStandardObjects();
            scope.defineFunctionProperties(Defaults.getNames(), Defaults.class, ScriptableObject.DONTENUM);

            context.setErrorReporter(new ErrorReporter() {
                            public void warning(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber) {
                                bus.log(String.format("Warning: %s %s %s %s %s ", detail, sourceName, lineNumber, lineSource, columnNumber));
                            }

                            public void error(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber) {
                                bus.log(String.format("Warning: %s %s %s %s %s ", detail, sourceName, lineNumber, lineSource, columnNumber));
                            }

                            public EvaluatorException runtimeError(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber) {
                                return new EvaluatorException(detail, sourceName, lineNumber, lineSource, columnNumber);
                            }
                        });


            JsMessageBusInit.createInScope(context, scope, new AbstractMessageBus() {
                @Override
                public void print(String message) {
                    
                }
            });
            loadRuntime(context, scope);

            Context.exit();

            super.setup(config, bus);


        } catch (IOException e) {
            throw new SetupFailedException(e);
        }
    }

    private void loadRuntime(Context cx, ScriptableObject env) throws IOException {
        loadScriptsTest(cx, env, "/lib/env.js");
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

    private static Script executeScript(Context cx, ScriptableObject env, Script script) {
        script.exec(cx, env);
        return script;
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


    @Override
    protected WebDriver createDriver() {
        return new RhinoWebDriver();
    }

    class RhinoWebDriver implements WebDriver {
        public void close() {

        }

        public void get(String location) {
            Context context = Context.enter();
            context.evaluateString(scope, "window.location ='" + location + "';", "Load: " + location, 1, null);
            Context.exit();
          //  context.evaluateString(scope, "window.QUnit.start();", "Reinit qunit", 1, null);
        }
    }
}
