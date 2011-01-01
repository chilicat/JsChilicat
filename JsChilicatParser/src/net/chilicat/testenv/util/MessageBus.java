package net.chilicat.testenv.util;

/**
 */
public interface MessageBus {
    public void print(String message);

    public void println(String message);

    public void log(String log);

    public void testAdded(String testName);

    public void moduleAdded(String moduleName);

    public void testStarted(String started);

    public void testPassed(String testName);

    public void testFailed(String testName, String errorMessage);

    public void moduleStart(String moduleName);

    public void moduleDone(String moduleName);

    public void testSuitStart(String packageName);

    public void testScriptStart(String scriptName);

    public void testScriptDone(String scriptName);

    public void testSuitDone(String packageName);
}
