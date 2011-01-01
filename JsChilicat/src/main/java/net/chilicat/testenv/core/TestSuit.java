package net.chilicat.testenv.core;

import java.io.File;
import java.util.List;

/**
 */
public interface TestSuit {
    public TestSetup getSetup();

    public List<File> getTestCases();

    public TestServer getTestServer();

    public String getPackage();
}
