package net.chilicat.testenv.utils;

import java.util.Collection;

/**
 */
final class CompositeMessageBus implements MessageBus {
    private final Collection<MessageBus> busList;

    public CompositeMessageBus(Collection<MessageBus> busList) {
        if (busList == null) {
            throw new NullPointerException("busList");
        }
        this.busList = busList;
    }

    public void print(String message) {
        for (MessageBus b : busList) {
            b.print(message);
        }
    }

    public void println(String message) {
        for (MessageBus b : busList) {
            b.println(message);
        }
    }

    public void log(String log) {
        for (MessageBus b : busList) {
            b.log(log);
        }
    }

    public void testAdded(String testName) {
        for (MessageBus b : busList) {
            b.testAdded(testName);
        }
    }

    public void moduleAdded(String moduleName) {
        for (MessageBus b : busList) {
            b.moduleAdded(moduleName);
        }
    }

    public void testStarted(String started) {
        for (MessageBus b : busList) {
            b.testStarted(started);
        }
    }

    public void testPassed(String testName) {
        for (MessageBus b : busList) {
            b.testPassed(testName);
        }
    }

    public void testFailed(String testName, String errorMessage) {
        for (MessageBus b : busList) {
            b.testFailed(testName, errorMessage);
        }
    }

    public void moduleStart(String moduleName) {
        for (MessageBus b : busList) {
            b.moduleStart(moduleName);
        }
    }

    public void moduleDone(String moduleName) {
        for (MessageBus b : busList) {
            b.moduleDone(moduleName);
        }
    }

    public void testSuitStart(String packageName) {
        for (MessageBus b : busList) {
            b.testSuitStart(packageName);
        }
    }

    public void testScriptStart(String scriptName) {
        for (MessageBus b : busList) {
            b.testScriptStart(scriptName);
        }
    }

    public void testScriptDone(String scriptName) {
        for (MessageBus b : busList) {
            b.testScriptDone(scriptName);
        }
    }

    public void testSuitDone(String packageName) {
        for (MessageBus b : busList) {
            b.testSuitDone(packageName);
        }
    }
}