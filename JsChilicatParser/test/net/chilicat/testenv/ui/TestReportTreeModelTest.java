package net.chilicat.testenv.ui;

import junit.framework.Assert;
import net.chilicat.testenv.model.BasicReportModelTests;
import net.chilicat.testenv.model.DefaultTestReportModel;
import net.chilicat.testenv.model.Module;
import net.chilicat.testenv.model.TestCase;
import org.junit.Test;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: dkuffner
 * Date: 27.06.2010
 * Time: 19:06:50
 * To change this template use File | Settings | File Templates.
 */
public class TestReportTreeModelTest {
    @Test
    public void testModel() throws Exception {
        final DefaultTestReportModel report = new DefaultTestReportModel();
        MockTracker mockTracker = new MockTracker();

        final TestReportTreeModel model = new TestReportTreeModel(report);
        model.setTracker(mockTracker);

        BasicReportModelTests.testStartAndEnded(report, report.getMessageBus());

        // No check that for each element a node exist.
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                List<Module> modules = report.getModules();
                for (Module module : modules) {
                    Assert.assertNotNull(model.getParentNodeFor(module));
                    for (TestCase testCase : module) {
                        Assert.assertNotNull(model.getParentNodeFor(testCase));
                    }
                }
            }
        });
    }

    private static class MockTracker implements TestReportTreeModel.TestCaseTracker {
        public void currentTest(Node node) {

        }
    }
}
