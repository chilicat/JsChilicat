package net.chilicat.testenv.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 */
public class Module extends Element implements Iterable<TestCase> {
    List<TestCase> testCases = new ArrayList<TestCase>();
    int test = 0;

    public Module(String moduleName, String curentPackageName, String curentScript) {
        addAttribute(Long.toString(System.currentTimeMillis()));
        addAttribute(moduleName == null ? "<unknown_module>" : moduleName);
        addAttribute(curentPackageName == null ? "<unknown_package>" : curentPackageName);
        addAttribute(curentScript == null ? "<unknown_script>" : curentScript);

    }

    public String getPackageName() {
        return attributeAt(2);
    }

    public String getScriptName() {
        return new File(attributeAt(3)).getName();
    }

    public String getFullScriptName() {
        return attributeAt(3);
    }

    public Iterator<TestCase> iterator() {
        return testCases.iterator();
    }

    public void addTest(TestCase element) {
        element.setParent(this);
        testCases.add(element);
    }

    @Override
    void ended(boolean b) {
        boolean passed = true;
        for (TestCase testCase : testCases) {
            if (!testCase.passed()) {
                passed = false;
                break;
            }
        }
        super.ended(passed);
    }

    @Override
    public String toString() {
        return getName();
    }

    public TestCase getTest(int testIndex) {
        return testCases.get(testIndex);
    }

    public TestCase testStarted() {
        TestCase test1 = testCases.get(test);
        test1.started();
        return test1;
    }

    public TestCase testEnded(boolean succes) {
        TestCase test1 = testCases.get(test);
        test1.ended(succes);
        test++;
        return test1;
    }

    public String getName() {
        return attributeAt(1);
    }

    public int testCount() {
        return testCases.size();
    }

    public int getErrors() {
        int errors = 0;
        for (TestCase testCase : testCases) {
            if (!testCase.passed()) {
                errors++;
            }
        }
        return errors;
    }
}
