package net.chilicat.testenv.report;

import net.chilicat.testenv.core.TestConfig;
import net.chilicat.testenv.core.resource.ResourceHelper;
import net.chilicat.testenv.coverage.Coverage;
import net.chilicat.testenv.model.TestReportModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 */
public class ReportFactory {

    public static void generateHTMLCoverageReport(Coverage coverage, ResourceHelper resourceHelper) throws IOException {
        new HtmlCoverageReport().generate(coverage, resourceHelper);
    }

    public static void generateJUnitReport(TestReportModel model, TestConfig config) throws IOException {
        new JUnitReport(model).generate(config.getWorkingDirectory());
    }

    public static void generateDefaultErrorReport(TestReportModel model, TestConfig config) throws IOException {
        Writer w = createReportFile(config.getWorkingDirectory());
        new FailedTestsReport(model).generate(w);
    }

    private static FileWriter createReportFile(File outDir) throws IOException {
        return createFile(outDir, "report.txt");
    }

    private static FileWriter createFile(File outDir, String name) throws IOException {
        FileWriter writer = null;
        File errorFile = new File(outDir, name);

        if (errorFile.exists()) {
            if (!errorFile.delete()) {
                throw new IOException("Cannot create " + name);
            }
        }

        writer = new FileWriter(errorFile);
        return writer;
    }

}
