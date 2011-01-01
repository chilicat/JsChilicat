package net.chilicat.testenv.report;

import net.chilicat.testenv.model.LogEntry;
import net.chilicat.testenv.model.Module;
import net.chilicat.testenv.model.TestCase;
import net.chilicat.testenv.model.TestReportModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Will write JUnit test reports for ANT.
 */
final class JUnitReport {
    private final TestReportModel model;

    private final SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd");
    private final SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");

    public JUnitReport(TestReportModel model) {
        if (model == null) {
            throw new NullPointerException("model");
        }
        this.model = model;
    }

    private String now() {
        Date now = new Date();
        return (date.format(now) + "T" + time.format(new Date()));
    }

    public void generate(File dir) throws IOException {
        final ResultFactory factory = new DirResultFactory(dir);

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilder();

            for (Suit suit : toSuites(model.getModules())) {
                final Document doc = documentBuilder.newDocument();
                final Element suitElement = newSuit(suit, doc);

                for (TestItem item : suit) {
                    final Element testCase = newTestCase(doc, item);
                    suitElement.appendChild(testCase);
                }

                doc.appendChild(suitElement);

                final Transformer transformer = transformerFactory.newTransformer();
                final DOMSource source = new DOMSource(doc);
                transformer.transform(source, factory.create(suit.getPackage(), suit.getName()));
            }
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        }
        catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

    private DocumentBuilder documentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        return documentBuilderFactory.newDocumentBuilder();
    }

    private Element newSuit(Suit suit, Document doc) {
        Element suitElement = doc.createElement("testsuite");
        suitElement.setAttribute("errors", suit.getErrors());
        suitElement.setAttribute("failures", suit.getFailures());
        suitElement.setAttribute("name", suit.getClassName());
        suitElement.setAttribute("tests", suit.getTestCount());
        suitElement.setAttribute("time", suit.getDuration());

        suitElement.setAttribute("timestamp", now());

        // add log outout.
        // / <system-out><![CDATA[Rotation is simulated for a four spark engine with an angle of 0?. 71 ]]></system-out>

        Element out = doc.createElement("system-out");
        out.appendChild(doc.createCDATASection(suit.getOutput()));
        suitElement.appendChild(out);

        return suitElement;
    }

    private Element newTestCase(Document doc, TestItem item) {
        Element testCase = doc.createElement("testcase");
        testCase.setAttribute("classname", item.getClassName());
        testCase.setAttribute("name", item.getName());
        testCase.setAttribute("time", item.getDuration());

        if (item.failed()) {
            Element error = newError(doc, item);
            testCase.appendChild(error);
        }

        return testCase;
    }

    private Element newError(Document doc, TestItem item) {
        Element error = doc.createElement("error");
        error.setAttribute("message", item.getErrorMessage());
        error.appendChild(doc.createTextNode(item.getErrorLog()));
        return error;
    }

    private double toTime(long time) {
        return Long.valueOf(time).doubleValue() / 1000.0;
    }

    private List<Suit> toSuites(List<Module> modules) {
        if (!modules.isEmpty()) {
            List<Suit> suits = new ArrayList<Suit>();
            Suit s = new Suit();
            for (Module m : modules) {
                if (s.acceptModule(m)) {
                    s.add(m);
                } else {
                    suits.add(s);
                    s = new Suit();
                    s.add(m);
                }
            }

            suits.add(s);
            return suits;
        }
        return Collections.emptyList();
    }

    private static String toPath(String a, String b) {
        String res = a;
        if (res.length() > 0) {
            res += ".";
        }
        return res + b;
    }

    class TestItem {
        private final TestCase c;
        private final String className;

        public TestItem(TestCase c, String className) {
            this.c = c;
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public String getName() {
            return c.getName();
        }

        public String getDuration() {
            return Double.toString(toTime(c.getDuration()));
        }

        public boolean failed() {
            return !c.passed();
        }

        public String getErrorMessage() {
            return c.getErrorMessage();
        }

        public String getErrorLog() {
            StringBuilder b = new StringBuilder();
            for (LogEntry s : c.logs()) {
                b.append(s.getName()).append("\n");
            }
            return b.toString();
        }
    }

    class Suit implements Iterable<TestItem> {
        private final List<Module> modules = new ArrayList<Module>();

        public boolean acceptModule(Module m) {
            return modules.isEmpty() ||
                    (modules.get(0).getPackageName().equals(m.getPackageName()) &&
                            modules.get(0).getScriptName().equals(m.getScriptName()));
        }

        public void add(Module m) {
            assert acceptModule(m);
            modules.add(m);
        }

        public String getErrors() {
            int errors = 0;
            for (Module m : modules) {
                errors += m.getErrors();
            }
            return Integer.toString(errors);
        }

        public String getFailures() {
            int errors = 0;
            return Integer.toString(errors);
        }

        public String getPackage() {
            return modules.get(0).getPackageName();
        }

        public String getName() {
            return removeExtension(modules.get(0).getScriptName());
        }

        public String getTestCount() {
            int count = 0;
            for (Module m : modules) {
                count += m.testCount();
            }
            return Integer.toString(count);
        }

        public String getDuration() {
            double duration = 0.0;
            for (Module m : modules) {
                duration += toTime(m.getDuration());
            }
            return Double.toString(duration);
        }

        public Iterator<TestItem> iterator() {
            List<TestItem> items = new ArrayList<TestItem>();
            for (Module m : modules) {
                for (TestCase c : m) {
                    items.add(new TestItem(c, getClassName()));
                }
            }
            return items.iterator();
        }

        public String getClassName() {
            return toPath(getPackage(), getName());
        }

        public String getOutput() {
            StringBuilder b = new StringBuilder();
            DateFormat f = SimpleDateFormat.getDateTimeInstance();

            for (Module m : modules) {
                for (TestCase t : m) {
                    for (LogEntry e : t.logs()) {
                        String time = f.format(new Date(e.getTime()));
                        b.append(time).append(" - ").append(e.getName()).append("\n");
                    }
                }
            }
            return b.toString();
        }
    }

    static interface ResultFactory {
        public Result create(String packageName, String scriptName) throws IOException;
    }

    static class DirResultFactory implements ResultFactory {
        private File dir;

        DirResultFactory(File dir) {
            if (dir == null) {
                throw new NullPointerException("dir"); //NonNls
            }

            this.dir = dir;
        }

        public Result create(String packageName, String scriptName) throws IOException {
            scriptName = removeExtension(scriptName);
            File file = new File(dir, String.format("TEST-%s.xml", toPath(packageName, scriptName)));
            FileWriter writer = new FileWriter(file);
            return new StreamResult(writer);
        }
    }

    private static String removeExtension(String scriptName) {
        int index = scriptName.lastIndexOf(".");
        if (index != -1) {
            scriptName = scriptName.substring(0, index);
        }
        return scriptName;
    }

}
