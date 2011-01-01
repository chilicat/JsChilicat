package net.chilicat.testenv.report;

import net.chilicat.testenv.model.LogEntry;
import net.chilicat.testenv.model.Module;
import net.chilicat.testenv.model.TestCase;
import net.chilicat.testenv.model.TestReportModel;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Logger;

/**
 */
final class FailedTestsReport {
    private final List<Module> modules;

    public FailedTestsReport(TestReportModel model) {
        if (model == null) {
            throw new NullPointerException("modules"); //NonNls
        }

        this.modules = model.getModules();
    }

    public void generate(Writer writer) throws IOException {
        Logger logger = Logger.getLogger(FailedTestsReport.class.getName());

        StringBuffer outBuffer = new StringBuffer();
        int failed = 0;
        int total = 0;




        for (Module m : modules) {
            if (!m.passed()) {
                outBuffer.append("\nModule: ").append(m.getScriptName()).append(" -- ").append(m.getName());

                for (TestCase t : m) {
                    if (!t.passed()) {
                        outBuffer.append("\n\tTest: ").append(t.getName()).append(" failed (").append(t.getState()).append(")");
                        for (LogEntry log : t.logs()) {
                            String logOut = log.attributeAt(1);
                            outBuffer.append("\n\t\tLog: ").append(logOut);
                        }

                        failed++;
                    }
                    total++;
                }
                outBuffer.append("\nfailed\n");
            } else {
                total += m.testCount();
            }
        }

        outBuffer.append("\n\nResult: ").append(failed).append(" of ").append(total).append(" Test(s) failed");
        if (failed > 0) {
            logger.warning(outBuffer.toString());
        } else {
            logger.info(outBuffer.toString());
        }
        writer.append(outBuffer.toString());
    }
}
