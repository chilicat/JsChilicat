package net.chilicat.testenv;

import net.chilicat.cmd.CommandArguments;
import net.chilicat.cmd.Type;
import net.chilicat.testenv.core.SetupFailedException;
import net.chilicat.testenv.core.TestUnitFramework;
import net.chilicat.testenv.utils.JSLFileHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public final class Main {
    private static final String WORKING_DIR = "workingDir";
    private static final String REMOTE = "remote";
    private static final String VERBOSE = "verbose";
    private static final String LIBS = "libs";
    private static final String SRC_TEST = "src-test";
    private static final String SRC = "src";
    private static final String PORT = "port";
    private static final String COVERAGE_REPORT = "coverage";
    private static final String JUNIT_REPORT = "junitReport";
    private static final String FRAMEWORK = "framework";

    private static final String SERVER = "server";
    private static final String TEST_TIMEOUT = "testTimeout";
    private static final String FIREFOX_PROFILE = "firefoxProfile";

    public static void main(String[] args) {
        try {
            CommandArguments argList = initProgramParameters(args);

            final List<File> libraries = toFileList(argList.getStrings(LIBS), "\nError: Library file doesn't exist: ");
            final List<File> testFiels = toFileList(argList.getStrings(SRC_TEST), "\nError: Test file doesn't exist: ");
            final List<File> sourceFiles = toFileList(argList.getStrings(SRC), "\nError: Source file doesn't exist: ");

            final String timeoutStr = argList.getString(TEST_TIMEOUT, "30");
            long testTimeout;
            try {
                testTimeout = Long.valueOf(timeoutStr) * 1000;
            } catch (NumberFormatException e) {
                throw new SetupFailedException("Illegal value for testTimeout. The testTimeout value must be a number.");
            }

            final ExecutionEnv env = new ExecutionEnv();
            env.setTestTimeout(testTimeout);
            env.setJunitReport(argList.getBoolean(JUNIT_REPORT, false));
            env.setCoverageReport(argList.getBoolean(COVERAGE_REPORT, false));
            env.setRemote(argList.getBoolean(REMOTE, false));
            env.setVerbose(argList.getBoolean(VERBOSE, false));
            env.setWorkingDirectory(initWorkingDirectory(argList));
            env.setServerFile(getServerFile(argList));
            env.setPort(argList.getInt(PORT, -1));
            env.setLibraryFiles(libraries);
            env.setSourceFiles(sourceFiles);
            env.setTestFiles(testFiels);
            env.setExecutorType(getExecutorType(argList));
            new Application().execute(env);


        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "", e);
            System.exit(-1);
        } catch (SetupFailedException e) {
            Logger.getAnonymousLogger().severe(e.getMessage());
            System.exit(-1);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Logger.getAnonymousLogger().severe(e.getMessage());
            System.exit(-1);
        }
    }

    private static ExecutorType getExecutorType(CommandArguments args) {
        for (ExecutorType type : ExecutorType.values()) {
            if (args.getBoolean(type.toString(), false)) {
                return type;
            }
        }

        return ExecutorType.chilicat;
    }

    private static File getServerFile(CommandArguments argList) {
        final String serverFileStr = argList.getString(SERVER, null);
        File serverFile = null;
        if (serverFileStr != null) {
            serverFile = new File(serverFileStr);
            if (!serverFile.exists() || !serverFile.isFile()) {
                throw new SetupFailedException("Server file does not exists or is not a file: " + serverFile);
            }
        }
        return serverFile;
    }

    private static File initWorkingDirectory(CommandArguments argList) throws IOException {
        String workingDir = argList.getString(WORKING_DIR, null);

        File reportOutput;
        if (workingDir != null) {
            reportOutput = new File(workingDir);
            if (!reportOutput.exists() && !reportOutput.mkdirs()) {
                throw new IOException("Cannot create directory for test report output: " + workingDir);
            }
        } else {
            reportOutput = File.createTempFile("jsChilicatWorkingDir", "").getParentFile();
            reportOutput = new File(reportOutput, "jsChilicat");
            if (!reportOutput.exists() && !reportOutput.mkdirs()) {
                throw new IOException("Cannot create directory for test report output: " + workingDir);
            }
        }

        return reportOutput;
    }

    private static CommandArguments initProgramParameters(String[] args) {
        CommandArguments argList = createCommandlineHelper();

        if (!argList.init(args)) {
            argList.logOptions();
            argList.printErrors(System.err);
            argList.printHelp(System.out);

            throw new SetupFailedException("Application settings");
        }

        argList.logOptions();

        return argList;
    }

    private static CommandArguments createCommandlineHelper() {
        CommandArguments argList = new CommandArguments();
        String fileList = String.format("<file>[%s<file>%s...]", File.pathSeparator, File.pathSeparator);

        argList.option(SRC).type(Type.MUST).fileList(true).hasArgument(true).desc("List of source directories or files").help(fileList);
        argList.option(SRC_TEST).type(Type.MUST).fileList(true).hasArgument(true).desc("List of test directories or files").help(fileList);
        argList.option(LIBS).type(Type.OPTIONAL).fileList(true).hasArgument(true).desc("List of test directories or files").help(fileList);
        argList.option(VERBOSE).type(Type.OPTIONAL).hasArgument(false).help("").desc("Verbose mode").hide(true);
        argList.option(WORKING_DIR).type(Type.OPTIONAL).hasArgument(true).help("<directory>").desc("Specify working directory. In the working directory will be test results stored. If option not specified no results will be generated");
        argList.option(REMOTE).type(Type.OPTIONAL).hasArgument(false).desc("Will print special format for intellij plugin/parser").hide(true);
        argList.option(PORT).type(Type.OPTIONAL).hasArgument(true).help("8182").desc("Set port which should be used by internal Http server. Please note: If port in used than application will fail. If <port> is not set than application tries automatically to find a unused port. Default Port is 8182");
        argList.option(COVERAGE_REPORT).type(Type.OPTIONAL).hasArgument(false).desc("Enables code coverage report");
        argList.option(JUNIT_REPORT).type(Type.OPTIONAL).hasArgument(false).desc("Enables junit report output.");
        argList.option(TEST_TIMEOUT).type(Type.OPTIONAL).hasArgument(true).desc("Specifies the timeout în seconds per test file. Default is 30 seconds");

        argList.option(FIREFOX_PROFILE).type(Type.OPTIONAL).hasArgument(true).desc("Specifies the firefox profile directory.");

        for (ExecutorType type : ExecutorType.values()) {
            argList.option(type.toString()).type(Type.OPTIONAL).hasArgument(false).desc(type.getDoc()).hide(type.isHidden());
        }

        argList.option(SERVER).type(Type.OPTIONAL).hasArgument(true).desc("Define a java script http server file.");

        String txt = String.format("Select a test unit framework (Default: %s). Supported frameworks are: ", TestUnitFramework.qunit);
        StringBuilder frameworkDesc = new StringBuilder(txt);

        for (TestUnitFramework framework : TestUnitFramework.values()) {
            if (frameworkDesc.length() > txt.length()) {
                frameworkDesc.append(", ");
            }
            frameworkDesc.append(framework.toString());
        }
        argList.option(FRAMEWORK).type(Type.OPTIONAL).hasArgument(true).desc(frameworkDesc.toString()).help(TestUnitFramework.qunit.toString());
        return argList;
    }

    private static List<File> toFileList(List<String> files, String errorMessage) throws IOException {
        List<File> libFileList = new ArrayList<File>();
        StringBuilder error = new StringBuilder();
        for (String fileStr : files) {
            final File file = new File(fileStr);
            libFileList.addAll(expandFiles(errorMessage, error, file));
        }

        if (error.length() > 0) {
            System.err.println(error);
            throw new SetupFailedException(error.toString());
        }
        return libFileList;
    }


    private static List<File> expandFiles(String errorMessage, StringBuilder error, File file) throws IOException {
        if (!file.exists()) {
            error.append(errorMessage).append(file.getPath());
            return Collections.emptyList();
        }

        if (file.getName().endsWith(".jsl")) {
            final List<File> read = new JSLFileHandler(file).read();
            for (File tmp : read) {
                if (!tmp.exists()) {
                    error.append(errorMessage).append(tmp.getPath());
                }
            }
            return read;
        } else {
            return Collections.singletonList(file);
        }
    }

}
