package net.chilicat.testenv.util;

/**
 */
public abstract class AbstractMessageBus implements MessageBus {

    public abstract void print(String message);

    private boolean delegate = true;

    protected AbstractMessageBus() {
        this(true);
    }

    protected AbstractMessageBus(boolean delegate) {
        this.delegate = delegate;
    }

    public void println(String message) {
        print(message + "\n");
    }

    public void log(String log) {
        if (delegate) {
            println(log);
        }
    }

    public void testAdded(String testName) {
        if (delegate) {
            println(testName);
        }
    }

    public void moduleAdded(String moduleName) {
        if (delegate) {
            println(moduleName);
        }
    }

    public void testStarted(String started) {
        if (delegate) {
            println(started);
        }
    }

    public void testPassed(String testName) {
        if (delegate) {
            println(testName);
        }
    }

    public void testFailed(String testName, String errorMessage) {
        if (delegate) {
            println(testName + " " + errorMessage);
        }
    }

    public void moduleStart(String moduleName) {
        if (delegate) {
            println(moduleName);
        }
    }

    public void moduleDone(String moduleName) {
        if (delegate) {
            println(moduleName);
        }
    }

    public void testSuitStart(String packageName) {
        if (delegate) {
            println(packageName);
        }
    }

    public void testScriptStart(String scriptName) {
        if (delegate) {
            println(scriptName);
        }
    }

    public void testScriptDone(String scriptName) {
        if (delegate) {
            println(scriptName);
        }
    }

    public void testSuitDone(String packageName) {
        if (delegate) {
            println(packageName);
        }
    }
}
