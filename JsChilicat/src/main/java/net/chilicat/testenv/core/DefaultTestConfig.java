package net.chilicat.testenv.core;

import net.chilicat.testenv.server.HttpServer;
import net.chilicat.testenv.util.MessageBus;
import net.chilicat.testenv.util.MessageBusFactory;

import java.io.File;
import java.util.List;

/**
 */
public class DefaultTestConfig implements TestConfig {
    private final List<File> libraries;
    private final List<File> sources;
    private final List<File> tests;
    private final HttpServer server;
    private final File workingDirectory;
    private final File jsServerFile;

    private final TestUnitFramework framework;

    public DefaultTestConfig(List<File> libraries, List<File> sources, List<File> tests, HttpServer server, File workingDirectory, File serverFile, TestUnitFramework framework) {
        if (libraries == null) {
            throw new NullPointerException("libraries");
        }
        if (sources == null) {
            throw new NullPointerException("sources");
        }
        if (tests == null) {
            throw new NullPointerException("tests");
        }
        if (server == null) {
            throw new NullPointerException("server");
        }

        this.libraries = libraries;
        this.sources = sources;
        this.tests = tests;
        this.server = server;
        this.workingDirectory = workingDirectory;
        this.framework = framework;
        this.jsServerFile = serverFile;
    }

    public File getServerFile() {
        return jsServerFile;
    }

    public TestUnitFramework getFramework() {
        return framework;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public HttpServer getServer() {
        return server;
    }

    public List<File> getLibraries() {
        return libraries;
    }

    public List<File> getSources() {
        return sources;
    }

    public List<File> getTestBase() {
        return tests;
    }
}
