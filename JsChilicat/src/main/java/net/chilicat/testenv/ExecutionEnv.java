package net.chilicat.testenv;

import net.chilicat.testenv.core.TestConfig;
import net.chilicat.testenv.core.TestExecutor;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (c) 2010 <chilicat>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p/>
 * User: Chilicat
 * Date: 13.11.2010
 * Time: 11:34:58
 */
public class ExecutionEnv {
    private boolean junitReport = false;
    private boolean coverageReport = false;
    private int port = -1;
    private boolean verbose = false;
    private boolean remote = false;
    private File workingDirectory;
    private File serverFile;
    private long testTimeout = 30000;

    private ExecutorType executorType = ExecutorType.chilicat;
    private List<File> sourceFiles = Collections.emptyList();
    private List<File> testFiles = Collections.emptyList();
    private List<File> libraryFiles = Collections.emptyList();

    private File firefoxProfile;

    public ExecutionEnv() {
    }

    public long getTestTimeout() {
        return testTimeout;
    }

    public void setTestTimeout(long testTimeout) {
        this.testTimeout = testTimeout;
    }

    public boolean isJUnitReport() {
        return junitReport;
    }

    public void setJunitReport(boolean junitReport) {
        this.junitReport = junitReport;
    }

    public boolean isCoverageReport() {
        return coverageReport;
    }

    public void setCoverageReport(boolean coverageReport) {
        this.coverageReport = coverageReport;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public File getServerFile() {
        return serverFile;
    }

    public void setServerFile(File serverFile) {
        this.serverFile = serverFile;
    }

    public List<File> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<File> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public List<File> getTestFiles() {
        return testFiles;
    }

    public void setTestFiles(List<File> testFiles) {
        this.testFiles = testFiles;
    }

    public List<File> getLibraryFiles() {
        return libraryFiles;
    }

    public void setLibraryFiles(List<File> libraryFiles) {
        this.libraryFiles = libraryFiles;
    }

    public ExecutorType getExecutorType() {
        return executorType;
    }

    public void setExecutorType(ExecutorType executorType) {
        this.executorType = executorType;
    }


    public void setFirefoxProfile(File firefoxProfile) {
        this.firefoxProfile = firefoxProfile;
    }

    public File getFirefoxProfile() {
        return firefoxProfile;
    }
}
