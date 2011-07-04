package net.chilicat.testenv.utils;

/**
 * Created by IntelliJ IDEA.
 * User: dkuffner
 * Date: 14.09.2010
 * Time: 20:18:52
 * To change this template use File | Settings | File Templates.
 */
public class SynMessageBus implements MessageBus {
    private final MessageBus bus;

    public SynMessageBus(MessageBus bus) {
        if (bus == null) {
            throw new NullPointerException("bus");
        }
        this.bus = bus;
    }

    public synchronized void print(String message) {
        bus.print(message);
    }

    public synchronized void println(String message) {
        bus.println(message);
    }

    public synchronized void log(String log) {
        bus.log(log);
    }

    public synchronized void testAdded(String testName) {
        bus.testAdded(testName);
    }

    public synchronized void moduleAdded(String moduleName) {
        bus.moduleAdded(moduleName);
    }

    public synchronized void testStarted(String started) {
        bus.testStarted(started);
    }

    public synchronized void testPassed(String testName) {
        bus.testPassed(testName);
    }

    public synchronized void testFailed(String testName, String errorMessage) {
        bus.testFailed(testName, errorMessage);
    }

    public synchronized void moduleStart(String moduleName) {
        bus.moduleStart(moduleName);
    }

    public synchronized void moduleDone(String moduleName) {
        bus.moduleDone(moduleName);
    }

    public synchronized void testSuitStart(String packageName) {
        bus.testSuitStart(packageName);
    }

    public synchronized void testScriptStart(String scriptName) {
        bus.testScriptStart(scriptName);
    }

    public synchronized void testScriptDone(String scriptName) {
        bus.testScriptDone(scriptName);
    }

    public synchronized void testSuitDone(String packageName) {
        bus.testSuitDone(packageName);
    }
}
