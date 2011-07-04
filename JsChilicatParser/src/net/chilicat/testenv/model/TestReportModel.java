package net.chilicat.testenv.model;

import net.chilicat.testenv.utils.MessageBus;

import java.util.List;

/**
 */
public interface TestReportModel {
    public void addTestReportModelListener(TestReportModelListener l);

    public void removeTestReportModelListener(TestReportModelListener l);

    public MessageBus getMessageBus();

    public List<Module> getModules();
    //public List<Test> getTestCases(Module module);

    public List<LogEntry> getLogEntries();

    public List<LogEntry> getLogEntries(Element element);


    //public boolean failed(Element element);

    Module getModule(int index);

    boolean allTestsPassed();
}
