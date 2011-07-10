package net.chilicat.testenv.core;

import net.chilicat.testenv.server.HttpServer;

import java.io.File;
import java.util.List;

/**
 */
public interface TestConfig {

    /**
     * Get timeout in milliseconds
     * @return the time out. <1 means no timeout.
     */
    public long getTestTimeout();
    public TestUnitFramework getFramework();

    public File getWorkingDirectory();

    public List<File> getLibraries();

    public List<File> getSources();

    public List<File> getTestBase();

    public HttpServer getServer();

    public File getServerFile();
}
