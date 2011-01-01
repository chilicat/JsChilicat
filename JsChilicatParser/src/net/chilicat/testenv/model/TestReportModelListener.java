package net.chilicat.testenv.model;

import java.util.EventListener;

/**
 */
public interface TestReportModelListener extends EventListener {
    public void started(TestReportEvent event);

    public void ended(TestReportEvent event);

    public void added(TestReportEvent event);
}
