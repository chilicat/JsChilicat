package net.chilicat.testenv.model;

import net.chilicat.testenv.utils.AbstractMessageBus;
import net.chilicat.testenv.utils.MessageBus;
import net.chilicat.testenv.utils.MessageBusFactory;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 */
public class DefaultTestReportModel implements TestReportModel {
    private final MessageBus bus;
    private final List<Module> modules = new ArrayList<Module>();

    private final EventListenerList listeners = new EventListenerList();

    private final List<LogEntry> completeOutPut = new ArrayList<LogEntry>();

    public DefaultTestReportModel() {
        this.bus = MessageBusFactory.sync(new Bus());
    }

    public MessageBus getMessageBus() {
        return bus;
    }

    public List<Module> getModules() {
        return modules;
    }

    public Module getModule(int index) {
        return modules.get(index);
    }

    public boolean allTestsPassed() {
        for (Module m : getModules()) {
            if (!m.passed()) {
                return false;
            }
        }
        return true;
    }

    public void addTestReportModelListener(TestReportModelListener l) {
        listeners.add(TestReportModelListener.class, l);
    }

    public void removeTestReportModelListener(TestReportModelListener l) {
        listeners.remove(TestReportModelListener.class, l);
    }

    protected void fireAdded(Element element) {
        TestReportEvent event = null;
        for (TestReportModelListener l : listeners.getListeners(TestReportModelListener.class)) {
            if (event == null) {
                event = new TestReportEvent(this, element);
            }
            l.added(event);
        }
    }

    protected void fireStarted(Element element) {
        TestReportEvent event = null;
        for (TestReportModelListener l : listeners.getListeners(TestReportModelListener.class)) {
            if (event == null) {
                event = new TestReportEvent(this, element);
            }
            l.started(event);
        }
    }

    protected void fireEnded(Element element) {
        TestReportEvent event = null;
        for (TestReportModelListener l : listeners.getListeners(TestReportModelListener.class)) {
            if (event == null) {
                event = new TestReportEvent(this, element);
            }
            l.ended(event);
        }
    }

    public List<LogEntry> getLogEntries() {
        return Collections.unmodifiableList(completeOutPut);

    }

    public List<LogEntry> getLogEntries(Element element) {
        if (element == null) {
            return getLogEntries();
        }

        if (element instanceof Module) {
            List<LogEntry> entries = new ArrayList<LogEntry>();
            entries.addAll(element.logs());

            for (TestCase t : (Module) element) {
                entries.addAll(t.logs());
            }

            return entries;
        }

        return element.logs();
    }

    class Bus extends AbstractMessageBus {
        private int currentModuleIndex = 0;
        private Element currentRunning;

        private String curentPackageName;
        private String curentScript;

        private boolean moduleAdded = false;

        @Override
        public void print(String message) {
            // nothing to
            log(message);
        }

        @Override
        public void testScriptStart(String scriptName) {
            moduleAdded = false;
            this.curentScript = scriptName;
            scriptScope.clear();
        }

        @Override
        public void testScriptDone(String scriptName) {
            // this.curentScript = null;
            StringBuilder b = new StringBuilder();
            b.append("\n\n=====================================\n");
            for(Module m : scriptScope) {
                
                b.append(String.format("\nModule: '%s' Test Cases: '%s'\n", m.getName(), m.testCount()));
                for(TestCase c : m) {
                    if(c.passed()) {
                        b.append("\n\t").append(String.format("Test Case Name: '%s' Error Message: \n\t\t %s", c.getName(), c.getErrorMessage()));
                    } else {
                        b.append("\n\t").append(String.format("Test Case Name: '%s' Passed", c.getName()));
                    }
                }
            }
            b.append("\n=====================================\n");

            Logger.getAnonymousLogger().info(b.toString());
        }

        @Override
        public void testSuitDone(String packageName) {
            this.curentPackageName = null;
        }

        @Override
        public void testSuitStart(String packageName) {
            this.curentPackageName = packageName;
        }

        List<Module> scriptScope = new ArrayList<Module>();

        @Override
        public void moduleAdded(String moduleName) {
            moduleAdded = true;
            Module module = new Module(moduleName, curentPackageName, curentScript);
            modules.add(module);

            scriptScope.add(module);

            fireAdded(module);
        }

        @Override
        public void testAdded(String testName) {
            if (!moduleAdded) {
                // Failover:
                // 1# Not all frameworks supports modules.
                // 2# QUnit test which will not define modules.
                moduleAdded(curentScript);
                moduleAdded = true;
            }

            Module m = modules.get(modules.size() - 1);
            TestCase testCase = new TestCase(testName);
            m.addTest(testCase);

            fireAdded(testCase);
        }

        @Override
        public void testStarted(String testName) {
            TestCase testCase = getCurrentModule().testStarted();
            assert testCase.getName().equals(testName);
            currentRunning = testCase;
            fireStarted(testCase);
        }

        @Override
        public void testPassed(String testName) {
            TestCase testCase = getCurrentModule().testEnded(true);
            assert testCase.getName().equals(testName);
            currentRunning = getCurrentModule();
            fireEnded(testCase);
        }

        @Override
        public void testFailed(String testName, String errorMessage) {
            TestCase testCase = getCurrentModule().testEnded(false);
            if (testCase == null) {
                return;
            }
            testCase.setErrorMessage(errorMessage);
            if (errorMessage.length() > 0) {
                log(errorMessage);
            }
            assert testCase.getName().equals(testName);
            currentRunning = getCurrentModule();
            fireEnded(testCase);
        }

        @Override
        public void moduleStart(String moduleName) {
            Module module = getCurrentModule();
            assert module.getName().equals(moduleName) : String.format("Unexpected Module. Current Module: '%s', Module: %s ", module.getName(), moduleName);

            module.started();

            currentRunning = module;
            fireStarted(module);
        }

        @Override
        public void moduleDone(String moduleName) {
            Module module = getCurrentModule();

            assert module.getName().equals(moduleName);

            module.ended(true);

            fireEnded(module);
            currentModuleIndex++;
            currentRunning = null;
        }

        @Override
        public void log(String log) {
            LogEntry entry = new LogEntry(log);
            completeOutPut.add(entry);

            if (currentRunning != null) {
                currentRunning.addLog(entry);
            }
        }

        private Module getCurrentModule() {
            return getModule(currentModuleIndex);
        }
    }
}
