package net.chilicat.testenv.model;

import net.chilicat.testenv.utils.MessageBus;
import org.junit.Test;

/**
 */
public class DefaultTestReportModelTest {
    @Test
    public void testAddModulesAndTests() throws Exception {
        DefaultTestReportModel model = new DefaultTestReportModel();
        BasicReportModelTests.testAddModulesAndTests(model, model.getMessageBus());
    }

    @Test
    public void testStartAndEnded() throws Exception {
        DefaultTestReportModel model = new DefaultTestReportModel();
        BasicReportModelTests.testStartAndEnded(model, model.getMessageBus());
    }


    @Test
    public void testAddAfterRunPossible() throws Exception {
        DefaultTestReportModel model = new DefaultTestReportModel();
        MessageBus bus = model.getMessageBus();

        BasicReportModelTests.testAddAfterRunPossible(model, bus);

    }

    @Test
    public void testEvents() throws Exception {
        DefaultTestReportModel model = new DefaultTestReportModel();
        MessageBus bus = model.getMessageBus();

        BasicReportModelTests.testEvents(model, bus);
    }


    @Test
    public void testLogs() throws Exception {
        DefaultTestReportModel model = new DefaultTestReportModel();
        MessageBus bus = model.getMessageBus();
        BasicReportModelTests.testLogs(model, bus);
    }
}
