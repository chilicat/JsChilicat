package net.chilicat.testenv.idea;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathUtil;
import net.chilicat.testenv.Main;
import net.chilicat.testenv.idea.ui.TestConsoleView;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 */
public class TestEnvRunProfileState extends JavaCommandLineState {

    private final TestEnvConfiguration config;


    public TestEnvRunProfileState(final @NotNull TestEnvConfiguration config, final @NotNull ExecutionEnvironment environment) {
        super(environment);
        this.config = config;
        setConsoleBuilder(new Builder(config, environment));
    }

    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        return super.execute(executor, runner);
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {

        JavaParameters javaParameters = new JavaParameters();
        Module module = config.getConfigurationModule().getModule();

        Sdk jdk = module == null ?
                ProjectRootManager.getInstance(config.getProject()).getProjectJdk() :
                ModuleRootManager.getInstance(module).getSdk();
        javaParameters.setJdk(jdk);

        javaParameters.setMainClass(Main.class.getName());
        javaParameters.getClassPath().add(PathUtil.getJarPathForClass(Main.class));

        String thisPath = PathUtil.getJarPathForClass(getClass());

        File myPlugin = new File(PathManager.getPluginsPath(), "JsChilicatIdea");

        if(!myPlugin.exists()) {
            throw new Error("Plugin dir:  " + myPlugin);
        }

        File lib = new File(myPlugin, "lib");

        if(!myPlugin.exists()) {
            throw new Error("lib dir:  " + lib);
        }


        for (File file : lib.listFiles()) {
            if (file.getName().endsWith(".jar") && !file.getAbsolutePath().equals(thisPath)) {
                javaParameters.getClassPath().add(file);
            }
        }

        javaParameters.setWorkingDirectory(config.getWorkingDirectory(true));

        javaParameters.getProgramParametersList().add("-workingDir", config.getWorkingDirectory(true));
        javaParameters.getProgramParametersList().add("-src", config.getSrcDir(true));
        javaParameters.getProgramParametersList().add("-src-test", config.getTestDir(true));
        javaParameters.getProgramParametersList().add("-remote"); // enable output for model parsing.
        javaParameters.getProgramParametersList().add("-framework", config.getFramework().toString()); // set test framework.
        javaParameters.getProgramParametersList().add("-" + config.getExecutionType().toString());

        if (config.isServerIsEnabled()) {
            javaParameters.getProgramParametersList().add("-server", config.getServerFile(true));
        }

        if (config.hasLibraries()) {
            javaParameters.getProgramParametersList().add("-libs", config.getLibrariesExpanded());
        }

        if (config.isCoverageSelected()) {
            javaParameters.getProgramParametersList().add("-coverage");
        }

        return javaParameters;
    }

    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        return super.startProcess();
    }

    private static class Builder extends TextConsoleBuilder {
        private final TestConsoleView view;

        public Builder(TestEnvConfiguration config, ExecutionEnvironment environment) {
            view = new TestConsoleView(config);
        }

        @Override
        public ConsoleView getConsole() {
            return view;
        }

        @Override
        public void addFilter(Filter filter) {

        }

        @Override
        public void setViewer(boolean b) {

        }
    }
}
