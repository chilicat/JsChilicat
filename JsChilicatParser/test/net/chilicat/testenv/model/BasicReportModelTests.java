package net.chilicat.testenv.model;

import net.chilicat.testenv.utils.MessageBus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 */
public class BasicReportModelTests {
    private static String[] tests = {"A", "B", "C"};
    private static String[] tests2 = {"D", "E", "F"};

    public static void testAddModulesAndTests(TestReportModel model, MessageBus bus) {

        bus.testSuitStart("net.chilicat");
        bus.testScriptStart("First.js");

        bus.moduleAdded("Module A");
        for (String name : tests) {
            bus.testAdded(name);
        }

        bus.testScriptDone("First.js");
        bus.testSuitDone("net.chilicat");

        bus.testSuitStart("net.chilicat");
        bus.testScriptStart("Secind.js");

        bus.moduleAdded("Module B");

        for (String name : tests2) {
            bus.testAdded(name);
        }

        bus.testScriptDone("Second.js");
        bus.testSuitDone("net.chilicat");

        assertEquals(2, model.getModules().size());

        for (int i = 0; i < tests.length; i++) {
            assertEquals(tests[i], model.getModule(0).getTest(i).getName());
        }

        for (int i = 0; i < tests2.length; i++) {
            assertEquals(tests2[i], model.getModule(1).getTest(i).getName());
        }
    }

    public static void testStartAndEnded(TestReportModel model, MessageBus bus) {
        bus.testScriptStart("Script A");
        bus.moduleAdded("Module A");
        for (String name : tests) {
            bus.testAdded(name);
        }
        bus.testScriptDone("Script A");

        bus.testScriptStart("Script B");
        bus.moduleAdded("Module B");
        for (String name : tests2) {
            bus.testAdded(name);
        }
        bus.testScriptDone("Script B");

        for (Module m : model.getModules()) {
            bus.moduleStart(m.getName());
            for (TestCase testCase : m) {
                bus.testStarted(testCase.getName());
                bus.testPassed(testCase.getName());
            }
            bus.moduleDone(m.getName());
        }
    }


    public static void testAddAfterRunPossible(TestReportModel model, MessageBus bus) {
        bus.moduleAdded("Module A");
        for (String name : tests) {
            bus.testAdded(name);
        }

        Module m1 = model.getModule(0);
        bus.moduleStart(m1.getName());
        for (TestCase testCase : m1) {
            bus.testStarted(testCase.getName());
            bus.testPassed(testCase.getName());
        }
        bus.moduleDone(m1.getName());

        bus.moduleAdded("Module B");
        for (String name : tests2) {
            bus.testAdded(name);
        }

        Module m2 = model.getModule(1);

        bus.moduleStart(m2.getName());
        for (TestCase testCase : m2) {
            bus.testStarted(testCase.getName());
            bus.testPassed(testCase.getName());
        }
        bus.moduleDone(m2.getName());
    }


    public static void testEvents(TestReportModel model, MessageBus bus) {
        TestListener listener = new TestListener(model);
        model.addTestReportModelListener(listener);
        String[] tests = {"A", "B", "C"};
        String[] tests2 = {"D", "E", "F"};


        listener.added = true;
        listener.name = "Module A";

        bus.moduleAdded("Module A");
        for (String name : tests) {
            listener.name = name;
            bus.testAdded(name);
        }

        listener.name = "Module B";
        bus.moduleAdded("Module B");

        for (String name : tests2) {
            listener.name = name;
            bus.testAdded(name);
        }


        listener.added = false;

        for (Module m : model.getModules()) {
            listener.started = true;
            listener.name = m.getName();
            bus.moduleStart(m.getName());

            for (TestCase testCase : m) {
                listener.started = true;
                listener.name = testCase.getName();
                bus.testStarted(testCase.getName());

                listener.started = false;
                listener.ended = true;

                bus.testPassed(testCase.getName());
            }

            listener.started = false;
            listener.ended = true;
            listener.name = m.getName();

            bus.moduleDone(m.getName());
        }
    }


    public static void testLogs(TestReportModel model, MessageBus bus) {
        String[] tests = {"A", "B", "C"};

        bus.moduleAdded("Module A");
        for (String name : tests) {
            bus.testAdded(name);
        }

        List<String> logList = new ArrayList<String>();

        for (Module m : model.getModules()) {
            bus.moduleStart(m.getName());

            bus.log(m.getName());
            logList.add(m.getName());

            for (TestCase testCase : m) {
                bus.testStarted(testCase.getName());

                bus.log(testCase.getName());
                logList.add(testCase.getName());

                bus.testPassed(testCase.getName());
            }

            bus.moduleDone(m.getName());
        }

        List<LogEntry> entries = model.getLogEntries(model.getModule(0));
        assertEquals(logList.size(), entries.size());

        for (int i = 0; i < logList.size(); i++) {
            assertEquals("\nExpected: " + logList.toString() + "\n Current: " + entries.toString(), logList.get(i), entries.get(i).getName());
        }

        for (Module m : model.getModules()) {
            for (TestCase t : m) {
                assertEquals(1, model.getLogEntries(t).size());
                assertEquals(t.getName(), model.getLogEntries(t).get(0).getName());
            }
        }
    }

    private static class TestListener implements TestReportModelListener {
        boolean started = false;
        boolean ended = false;
        boolean added = false;
        String name;

        private final TestReportModel model;

        private TestListener(TestReportModel model) {
            if (model == null) {
                throw new NullPointerException("model");
            }
            this.model = model;
        }

        public void started(TestReportEvent event) {
            assertEquals(model, event.getSource());
            assertTrue(started);
            assertEquals(name, event.getElement().getName());
        }

        public void ended(TestReportEvent event) {
            assertEquals(model, event.getSource());
            assertTrue(ended);
            assertEquals(name, event.getElement().getName());
        }

        public void added(TestReportEvent event) {
            assertEquals(model, event.getSource());
            assertTrue(added);
            assertEquals(name, event.getElement().getName());
        }
    }

}
