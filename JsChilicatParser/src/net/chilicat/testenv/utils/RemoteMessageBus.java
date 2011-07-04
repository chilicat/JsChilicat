package net.chilicat.testenv.utils;

import java.io.PrintStream;

/**
 */
final class RemoteMessageBus extends AbstractMessageBus {
    private final Printer out;

    public RemoteMessageBus(final PrintStream out) {
        this(new PrinterStreamWrapper(out));
    }

    public RemoteMessageBus(Printer out) {
        if (out == null) {
            throw new NullPointerException("out");
        }

        this.out = out;
    }

    private void send(String pattern, String... names) {
        for (int i = 0, namesLength = names.length; i < namesLength; i++) {
            names[i] = names[i].replace("'", "\"");
        }
        String msg = String.format(pattern, (Object[]) names);
        out.println(msg);
    }

    @Override
    public void moduleAdded(String moduleName) {
        send("MODULE_ADD: '%s'", moduleName);
    }

    @Override
    public void testAdded(String testName) {
        send("TEST_ADD: '%s'", testName);
    }

    @Override
    public void testStarted(String started) {
        send("TEST_START: '%s'", started);
    }

    @Override
    public void testPassed(String testName) {
        send("TEST_PASS: '%s'", testName);
    }

    @Override
    public void testFailed(String testName, String errorMessage) {
        send("TEST_FAILED: '%s' '%s'", testName, errorMessage);
    }

    @Override
    public void moduleStart(String moduleName) {
        send("MODULE_START: '%s'", moduleName);
    }

    @Override
    public void moduleDone(String moduleName) {
        send("MODULE_DONE: '%s'", moduleName);
    }

    @Override
    public void testSuitStart(String packageName) {
        send("SUIT_START: '%s'", packageName);
    }

    @Override
    public void testScriptStart(String scriptName) {
        send("SCRIPT_START: '%s'", scriptName);
    }

    @Override
    public void testScriptDone(String scriptName) {
        send("SCRIPT_DONE: '%s'", scriptName);
    }

    @Override
    public void testSuitDone(String packageName) {
        send("SUIT_DONE: '%s'", packageName);
    }

    @Override
    public void println(String message) {
        out.println(message);
    }

    @Override
    public void print(String message) {
        out.print(message);
    }

    @Override
    public void log(String log) {
        send("LOG: '%s'", log);
    }

    private static class PrinterStreamWrapper implements Printer {
        private final PrintStream out;

        public PrinterStreamWrapper(PrintStream out) {
            this.out = out;
        }

        public void println(String msg) {
            out.println(msg);
        }

        public void print(String message) {
            out.print(message);
        }
    }


}
