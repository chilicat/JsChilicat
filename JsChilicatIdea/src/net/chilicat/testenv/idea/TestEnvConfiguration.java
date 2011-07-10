package net.chilicat.testenv.idea;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import net.chilicat.testenv.ExecutorType;
import net.chilicat.testenv.core.TestUnitFramework;
import net.chilicat.testenv.idea.ui.RunProfileSettingsPane;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class TestEnvConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> {

    private String srcDir, testDir, workingDir;
    private String libraries;
    private TestUnitFramework framework = TestUnitFramework.qunit;
    private ExecutorType executionType = ExecutorType.chilicat;

    private boolean coverageSelected = false;

    private boolean serverIsEnabled = false;
    private String serverFile;
    private long testTimeout = 30;

    public TestEnvConfiguration(String name, Project project, ConfigurationFactory factory) {
        super(name, new RunConfigurationModule(project), factory);

    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RunProfileSettingsPane(this);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env)
            throws ExecutionException {
        return new TestEnvRunProfileState(this, env);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (srcDir == null || testDir == null || workingDir == null) {
            throw new RuntimeConfigurationException("Settings file is required");
        }
        if (!new File(getSrcDir(true)).exists()) {
            throw new RuntimeConfigurationException("Source location does not exist: " + srcDir);
        }
        if (!new File(getTestDir(true)).exists()) {
            throw new RuntimeConfigurationException("Test location does not exist: " + testDir);
        }
        if (!new File(getWorkingDirectory(true)).exists()) {
            throw new RuntimeConfigurationException("Working Directory does not exist: " + workingDir);
        }

        if (!new File(getWorkingDirectory(true)).isDirectory()) {
            throw new RuntimeConfigurationException("Working Directory must be a directory");
        }

        if (isServerIsEnabled() && !new File(getServerFile(true)).exists()) {
            throw new RuntimeConfigurationException("Server script does not exist: " + serverFile);
        }
    }

    public Collection<Module> getValidModules() {
        List<Module> modules = new ArrayList<Module>();
        Module[] allModules = ModuleManager.getInstance(getProject()).getModules();
        modules.addAll(Arrays.asList(allModules));
        return modules;
    }

    protected ModuleBasedConfiguration createInstance() {
        return new TestEnvConfiguration(getName(), getProject(), getFactory());
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        readModule(element);
        srcDir = JDOMExternalizer.readString(element, "sourceDir");
        testDir = JDOMExternalizer.readString(element, "testDir");
        workingDir = JDOMExternalizer.readString(element, "workingDir");
        coverageSelected = JDOMExternalizer.readBoolean(element, "coverageSelected");

        if (workingDir == null || workingDir.length() == 0) {
            workingDir = getProject().getBaseDir().getPath();
        }

        libraries = JDOMExternalizer.readString(element, "libs");

        resolveFramework(element);
        resolveExecutionType(element);

        serverIsEnabled = JDOMExternalizer.readBoolean(element, "serverIsEnabled");
        serverFile = JDOMExternalizer.readString(element, "serverFile");
        testTimeout = resolveTestTimeout(element);
    }

    private long resolveTestTimeout(Element element) {
        String tmpTimeout = JDOMExternalizer.readString(element, "testTimeout");
        long testTimout = 30;
        try {
            if(tmpTimeout != null) {
                testTimout = Long.valueOf(tmpTimeout);
            }
        } catch(NumberFormatException ignore) {
            Logger.getAnonymousLogger().log(Level.FINEST, "Wrong format: testTimeout.", ignore);
        }
        return testTimout;
    }

    private void resolveExecutionType(Element element) {
        executionType = ExecutorType.chilicat; // set default
        String et = JDOMExternalizer.readString(element, "executionType");
        if (et != null) {
            try {
                executionType = ExecutorType.valueOf(et);
            } catch (IllegalArgumentException e) {
                // ok, some old value?
                // just use default.
            }
        }
    }

    private void resolveFramework(Element element) {
        framework = TestUnitFramework.qunit; // set default
        String fw = JDOMExternalizer.readString(element, "framework");
        if (fw != null) {
            try {
                framework = TestUnitFramework.valueOf(fw);
            } catch (IllegalArgumentException e) {
                // ok, some old value?
                // just use default.
            }
        }
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        writeModule(element);
        JDOMExternalizer.write(element, "sourceDir", srcDir);
        JDOMExternalizer.write(element, "testDir", testDir);
        JDOMExternalizer.write(element, "libs", libraries);
        JDOMExternalizer.write(element, "workingDir", workingDir);

        JDOMExternalizer.write(element, "framework", framework.toString());
        JDOMExternalizer.write(element, "executionType", executionType.toString());
        JDOMExternalizer.write(element, "coverageSelected", coverageSelected);

        JDOMExternalizer.write(element, "serverIsEnabled", serverIsEnabled);
        JDOMExternalizer.write(element, "serverFile", serverFile);
        JDOMExternalizer.write(element, "testTimeout", String.valueOf(testTimeout));
    }

    public void setConfiguration(String srcDir, String testDir, String libraries, TestUnitFramework framework, ExecutorType executionType, boolean coverageSelected, String workingDir, long testTimeout) {
        this.srcDir = srcDir;
        this.testDir = testDir;
        this.libraries = libraries;
        this.framework = framework;
        this.executionType = executionType;
        this.coverageSelected = coverageSelected;
        this.workingDir = workingDir;
        this.testTimeout = testTimeout;
    }

    public void setServerConfig(boolean selected, String text) {
        this.serverIsEnabled = selected;
        this.serverFile = text;
    }

    private String expand(String str) {
        if (str != null && str.startsWith("./")) {
            VirtualFile baseDir = getProject().getBaseDir();
            if (baseDir == null) {
                return str;
            }

            File path = new File(baseDir.getPath());
            path = new File(path, str);
            return path.getAbsolutePath();
        }
        return str;
    }

    public String getSrcDir(boolean expand) {
        if (expand) {
            return expand(srcDir);
        }
        return srcDir;
    }

    public boolean isServerIsEnabled() {
        return serverIsEnabled;
    }

    public String getServerFile(boolean expand) {
        if (expand) {
            return expand(serverFile);
        }
        return serverFile;
    }

    public String getTestDir(boolean expand) {
        if (expand) {
            return expand(testDir);
        }
        return testDir;
    }

    public String getWorkingDirectory(boolean expand) {
        if (expand) {
            return expand(workingDir);
        }
        return workingDir;
    }

    public String getLibrariesExpanded() {
        StringBuffer b = new StringBuffer();
        for (String a : getLibraries().split("\\|")) {
            if (b.length() > 0) {
                b.append(File.pathSeparator);
            }

            b.append(expand(a));
        }

        return b.toString();
    }

    public String getLibraries() {
        return libraries;
    }

    public boolean hasLibraries() {
        return libraries != null && libraries.length() > 0;
    }

    public ExecutorType getExecutionType() {
        return executionType;
    }

    public TestUnitFramework getFramework() {
        return framework;
    }

    public boolean isCoverageSelected() {
        return coverageSelected;
    }

    public long getTestTimeout() {
        return testTimeout;
    }
}