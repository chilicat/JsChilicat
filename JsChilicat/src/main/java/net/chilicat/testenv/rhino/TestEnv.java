package net.chilicat.testenv.rhino;

import net.chilicat.testenv.utils.MessageBus;
import net.chilicat.testenv.utils.MessageBusFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class TestEnv {
    private static MessageBus messageBus = MessageBusFactory.nullBus();

    private final static Logger LOG = Logger.getLogger("TestEnv");

    public static MessageBus getMessageBus() {
        return messageBus;
    }

    public static void setMessageBus(MessageBus messageBus) {
        if (messageBus == null) {
            throw new NullPointerException("messageBus");
        }
        TestEnv.messageBus = new SaveMessageBus(messageBus);
    }

    public static void log_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (i > 0)
                b.append(" ");

            // Convert the arbitrary JavaScript value into a string form.
            b.append(Context.toString(args[i]));
        }
        String out = b.toString();

        if ("\n".equals(out) || out.length() == 0) {
            return;
        }

        messageBus.log(b.toString());
    }

    public static void test_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        messageBus.testAdded(getValue(args));
    }

    public static void module_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        messageBus.moduleAdded(getValue(args));
    }

    public static void testStart_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        messageBus.testStarted(getValue(args));
    }

    public static void testPass_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        messageBus.testPassed(getValue(args));
    }

    public static void testFail_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        String name = Context.toString(args[0]);
        String cause = args.length > 1 ? Context.toString(args[1]) : "";
        messageBus.testFailed(name, cause);
    }

    public static void moduleStart_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        messageBus.moduleStart(getValue(args));
    }

    public static void moduleDone_(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        messageBus.moduleDone(getValue(args));
    }

    public static String[] getNames() {
        return new String[]{"test_", "module_", "testStart_", "testPass_", "testFail_", "moduleDone_", "moduleStart_"};
    }

    private static String getValue(Object[] args) {
        if (args.length > 1) {
            return Context.toString(args[1]);
        } else {
            if (args.length == 1) {
                return Context.toString(args[0]);
            }
        }
        return "";
    }

    final static class SaveMessageBus implements MessageBus {
        private final MessageBus bus;

        SaveMessageBus(MessageBus bus) {
            if (bus == null) {
                throw new NullPointerException("bus");
            }
            this.bus = bus;
        }

        public void print(String message) {
            try {
                bus.print(message);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, message, e);
            }
        }

        public void println(String message) {
            try {
                bus.println(message);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, message, e);
            }
        }

        public void log(String log) {
            try {
                bus.log(log);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, log, e);
            }
        }

        public void testAdded(String testName) {
            try {
                bus.testAdded(testName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, testName, e);
            }
        }

        public void moduleAdded(String moduleName) {
            try {
                bus.moduleAdded(moduleName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, moduleName, e);
            }
        }

        public void testStarted(String started) {
            try {
                bus.testStarted(started);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, started, e);
            }
        }

        public void testPassed(String testName) {
            try {
                bus.testPassed(testName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "", e);
            }
        }

        public void testFailed(String testName, String errorMessage) {
            try {
                bus.testFailed(testName, errorMessage);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "", e);
            }
        }

        public void moduleStart(String moduleName) {
            try {
                bus.moduleStart(moduleName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, moduleName, e);
            }
        }

        public void moduleDone(String moduleName) {
            try {
                bus.moduleDone(moduleName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, moduleName, e);
            }
        }

        public void testSuitStart(String packageName) {
            try {
                bus.testSuitStart(packageName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, packageName, e);
            }
        }

        public void testScriptStart(String scriptName) {
            try {
                bus.testScriptStart(scriptName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, scriptName, e);
            }
        }

        public void testScriptDone(String scriptName) {
            try {
                bus.testScriptDone(scriptName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, scriptName, e);
            }
        }

        public void testSuitDone(String packageName) {
            try {
                bus.testSuitDone(packageName);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, packageName, e);
            }
        }
    }
}
