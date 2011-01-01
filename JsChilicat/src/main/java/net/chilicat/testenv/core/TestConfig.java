package net.chilicat.testenv.core;

import net.chilicat.testenv.server.HttpServer;
import net.chilicat.testenv.util.MessageBus;

import java.io.File;
import java.util.List;

/**
 */
public interface TestConfig {

    public TestUnitFramework getFramework();

    public File getWorkingDirectory();

    public List<File> getLibraries();

    public List<File> getSources();

    public List<File> getTestBase();

    public HttpServer getServer();

    public File getServerFile();
}
