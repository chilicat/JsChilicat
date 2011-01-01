package net.chilicat.testenv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class DefaultTestSuite implements TestSuit {
    private final List<File> files = new ArrayList<File>();
    private final TestSetup setup;
    private final TestServer server;
    private final String packageName;

    public DefaultTestSuite(TestSetup setup, TestServer server, String packageName) {
        if (setup == null) {
            throw new NullPointerException("setup"); //NonNls
        }
        if (server == null) {
            throw new NullPointerException("server"); //NonNls
        }
        if (packageName == null) {
            throw new NullPointerException("packageName"); //NonNls
        }
        this.setup = setup;
        this.server = server;
        this.packageName = packageName;
    }

    public String getPackage() {
        return packageName;
    }

    public TestServer getTestServer() {
        return server;
    }

    public TestSetup getSetup() {
        return setup;
    }

    public List<File> getTestCases() {
        return Collections.unmodifiableList(files);
    }

    public void add(File file) {
        files.add(file);
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    @Override
    public String toString() {
        return "SimpleTestSuite{" +
                "files=" + files +
                '}';
    }
}
