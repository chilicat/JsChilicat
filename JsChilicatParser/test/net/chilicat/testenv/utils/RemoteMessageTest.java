package net.chilicat.testenv.utils;

import net.chilicat.testenv.model.BasicReportModelTests;
import net.chilicat.testenv.model.DefaultTestReportModel;
import net.chilicat.testenv.model.TestReportModel;
import org.junit.Test;

/**
 */
public class RemoteMessageTest {

    private RemoteMessageBus createBus(TestReportModel model) {
        RemoteMessageParser parser = new RemoteMessageParser(model.getMessageBus());
        MockPrinter printer = new MockPrinter(parser);
        RemoteMessageBus remoteBus = new RemoteMessageBus(printer);
        return remoteBus;
    }


    @Test
    public void testAddModulesAndTests() throws Exception {
        TestReportModel model = new DefaultTestReportModel();
        RemoteMessageBus bus = createBus(model);
        BasicReportModelTests.testAddModulesAndTests(model, bus);
    }

    @Test
    public void testStartAndEnded() throws Exception {
        TestReportModel model = new DefaultTestReportModel();
        RemoteMessageBus bus = createBus(model);
        BasicReportModelTests.testStartAndEnded(model, bus);
    }


    @Test
    public void testAddAfterRunPossible() throws Exception {
        TestReportModel model = new DefaultTestReportModel();
        RemoteMessageBus bus = createBus(model);
        BasicReportModelTests.testAddAfterRunPossible(model, bus);

    }

    @Test
    public void testEvents() throws Exception {
        TestReportModel model = new DefaultTestReportModel();
        RemoteMessageBus bus = createBus(model);
        BasicReportModelTests.testEvents(model, bus);
    }


    @Test
    public void testLogs() throws Exception {
        TestReportModel model = new DefaultTestReportModel();
        RemoteMessageBus bus = createBus(model);
        BasicReportModelTests.testLogs(model, bus);
    }

    @Test
    public void testSomeRandomInput() throws Exception {
        TestReportModel model = new DefaultTestReportModel();
        RemoteMessageBus bus = createBus(model);

        bus.print("Hallo");
        bus.println("Ne w Stuff");
        bus.moduleAdded("A");
        bus.print("A Module was added");
        bus.testAdded("Test A");
        bus.print("A Test was added");
        bus.log("A log!!");

        bus.moduleStart("A");
        bus.print("Module Started");
        bus.println("Module Started");
        bus.log("Module Started");
        bus.testStarted("Test A");
        bus.print("Test Started");
        bus.println("Test Started");
        bus.log("TestStarted");
        bus.testPassed("Test A");

        bus.print("grr");
        bus.println("grr");
        bus.log("grr");

        System.out.println("");

    }


    private static class MockPrinter implements Printer {
        private final RemoteMessageParser parser;

        private MockPrinter(RemoteMessageParser parser) {
            this.parser = parser;
        }

        public void println(String msg) {
            parser.parse(msg + "\n");
        }

        public void print(String message) {
            parser.parse(message);
        }
    }
}
