package net.chilicat.testenv.core.testcollection;

import net.chilicat.testenv.core.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class AbstractTestCollector implements TestCollector {
    private final DefaultTestCollection col = new DefaultTestCollection();

    public TestCollection collect(TestConfig config) {
        for (File file : config.getTestBase()) {
            collect(file, config, "");
        }
        return col;
    }

    private void collect(File file, TestConfig config, String packageName) {
        if (!file.exists()) {
            throw new SetupFailedException("Source doesn't exist: " + file);
        }

        if (file.isDirectory()) {
            TestSetup setup = initSetup(file);
            TestServer server = initTestServer(file, config);
            DefaultTestSuite suite = new DefaultTestSuite(setup, server, packageName);

            List<File> dirs = new ArrayList<File>();

            for (File child : file.listFiles()) {
                if (!child.exists()) {
                    throw new SetupFailedException("Source doesn't exist: " + child);
                }
                if (child.isDirectory()) {
                    dirs.add(child);
                } else if (setup.getFilter().accept(child.getName())) {
                    suite.add(child);
                }
            }

            if (!suite.isEmpty()) {
                col.add(suite);
            }

            for (File dir : dirs) {
                if (packageName.length() > 0) {
                    packageName += ".";
                }
                packageName += dir.getName();
                collect(dir, config, packageName);
            }
        }
    }

    protected abstract TestServer initTestServer(File dir, TestConfig config);

    protected abstract TestSetup initSetup(File dir);
}
