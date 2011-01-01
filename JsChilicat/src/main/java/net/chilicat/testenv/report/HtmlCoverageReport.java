package net.chilicat.testenv.report;

import net.chilicat.testenv.core.SetupFailedException;
import net.chilicat.testenv.core.Utils;
import net.chilicat.testenv.core.resource.Resource;
import net.chilicat.testenv.core.resource.ResourceHelper;
import net.chilicat.testenv.coverage.Coverage;
import net.chilicat.testenv.coverage.LineReport;
import org.antlr.stringtemplate.StringTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 */
final class HtmlCoverageReport {

    public void generate(Coverage coverage, ResourceHelper resourceHelper) throws IOException {
        if (!coverage.isEnabled()) {
            return;
        }

        String site = Utils.readAsText(getClass().getResourceAsStream("/coverage/file_template.html"));

        Resource core = exportResource(resourceHelper, "syntaxhighlighter/scripts/shCore.js");
        Resource brush = exportResource(resourceHelper, "syntaxhighlighter/scripts/shBrushJScript.js");
        Resource style = exportResource(resourceHelper, "syntaxhighlighter/styles/jschilicat.css");

        DecimalFormat df = new DecimalFormat("0.00");
        StringTemplate template = new StringTemplate(site);

        IndexFile index = new IndexFile(resourceHelper.location("index.html"));

        for (LineReport report : coverage.getLineReports()) {
            File file = report.getResource().getFile();
            Resource htmlFile = resourceHelper.location(report.getResource(), file.getName() + ".html");
            String script = report.getResource().readAsText();

            template.setAttribute("core", relativePathTo(core, htmlFile));
            template.setAttribute("brush", relativePathTo(brush, htmlFile));
            template.setAttribute("style", relativePathTo(style, htmlFile));
            template.setAttribute("title", report.getResource().getFQN());

            String stringCov = df.format(report.getCoverage());
            template.setAttribute("coverage", stringCov);

            template.setAttribute("content", script);
            StringBuffer highlight = toHighlightString(report);
            template.setAttribute("highlights", highlight.toString());

            Utils.writeAsText(htmlFile.getFile(), template.toString());

            template.reset();

            index.add(report.getResource(), htmlFile, stringCov);
        }

        index.write();
    }

    private String relativePathTo(Resource resource, Resource htmlFile) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < htmlFile.getPackage().tokenCount(); i++) {
            b.append("../");
        }
        b.append(resource.getPackage().toPath());
        b.append("/").append(resource.getFile().getName());
        return b.toString();
    }

    private Resource exportResource(ResourceHelper resourceHelper, String path) throws IOException {
        Resource resource = resourceHelper.location(path);
        InputStream stream = getClass().getResourceAsStream("/coverage/" + path);
        if (stream == null) {
            throw new SetupFailedException("Cannot export resource: " + path);
        }
        resource.write(stream);
        return resource;
    }

    private StringBuffer toHighlightString(LineReport report) {
        StringBuffer highlight = new StringBuffer();
        for (int lineNo : report.getNotVisitedLines()) {

            if (highlight.length() != 0) {
                highlight.append(", ");

            }
            highlight.append(lineNo);
        }
        return highlight;
    }

    private class IndexFile {
        private final Resource index;
        private final StringTemplate link = new StringTemplate("<div class=\"file\"><a href=\"$link$\">$content$</div>");
        private final StringTemplate covTemp = new StringTemplate("<div class=\"coverage\">$content$</div>");

        private StringBuffer links = new StringBuffer();
        private StringBuffer cov = new StringBuffer();


        private IndexFile(Resource index) {
            this.index = index;
        }

        public void add(Resource orginal, Resource html, String coverage) {
            link.setAttribute("link", html.relativePath());
            link.setAttribute("content", orginal.getFQN());
            covTemp.setAttribute("content", coverage + " %");

            links.append(link.toString());
            cov.append(covTemp.toString());

            link.reset();
            covTemp.reset();
        }

        public void write() throws IOException {
            String tmp = Utils.readAsText(getClass().getResourceAsStream("/coverage/index_template.html"));
            StringTemplate t = new StringTemplate(tmp);
            t.setAttribute("links", links.toString());
            t.setAttribute("coverage", cov.toString());
            index.writeAsText(t.toString());
            t.reset();
        }
    }
}
