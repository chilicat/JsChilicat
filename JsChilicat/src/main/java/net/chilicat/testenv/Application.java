package net.chilicat.testenv;

import net.chilicat.testenv.core.*;
import net.chilicat.testenv.core.resource.ResourceHelper;
import net.chilicat.testenv.core.resource.Resources;
import net.chilicat.testenv.core.testcollection.SingleFileTestSuitCollector;
import net.chilicat.testenv.core.testcollection.TestCollection;
import net.chilicat.testenv.core.testcollection.TestCollector;
import net.chilicat.testenv.coverage.Coverage;
import net.chilicat.testenv.model.DefaultTestReportModel;
import net.chilicat.testenv.model.TestReportModel;
import net.chilicat.testenv.report.ReportFactory;
import net.chilicat.testenv.server.HttpServer;
import net.chilicat.testenv.utils.MessageBus;
import net.chilicat.testenv.utils.MessageBusFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Time: 11:37:15
 */
public class Application {
    public void execute(ExecutionEnv env) {
        HttpServer server = null;

        try {
            check(env);

            server = HttpServer.newServer(env.getPort());
            TestConfig config = new DefaultTestConfig(env.getLibraryFiles(), env.getSourceFiles(), env.getTestFiles(), server, env.getWorkingDirectory(), env.getServerFile(), TestUnitFramework.qunit);

            TestExecutor executor;
            try {
                executor = env.getExecutorType().create();
            } catch(LinkageError e) {
                throw new SetupFailedException(String.format("ExecutorType '%s' is not supported because web drivers are not installed.", env.getExecutorType().toString()), e);
            }

            TestCollector collector = new SingleFileTestSuitCollector();
            TestCollection collection = collector.collect(config);

            final TestReportModel model = new DefaultTestReportModel();
            final MessageBus messageBus = createMessageBus(env, model);

            if (env.isCoverageReport()) {
                executor.setCoverage(Coverage.createCoverage());
            }

            printSetup(config, collection);

            executor.setup(config, messageBus);
            try {
                for (TestSuit suit : collection.getTestSuits()) {
                    executor.execute(config, suit);
                }
            } finally {
                executor.dispose(config);
            }

            generateReports(model, config, executor.getCoverage(), env.isJUnitReport());

            System.exit(model.allTestsPassed() ? 0 : 1);
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
        } finally {
            if (server != null) {
                server.dispose();
            }
        }
    }

    private void printSetup(TestConfig config, TestCollection collection) {
        String pattern = "\n------------------\n %s \n------------------\n %s \n------------------";


        StringBuilder libs = new StringBuilder();
        for (File file : config.getLibraries()) {
            libs.append("\t").append(file.getPath()).append("\n");
        }

        StringBuilder source = new StringBuilder();
        for (File file : config.getSources()) {
            source.append("\t").append(file.getPath()).append("\n");
        }

        StringBuilder testFiles = new StringBuilder();
        for (TestSuit suit : collection.getTestSuits()) {
            for (File f : suit.getTestCases()) {
                testFiles.append("\t").append(f.getPath()).append("\n");
            }
        }

        Logger.getAnonymousLogger().info(String.format(pattern, "Lib Files", libs) + "\n"
                + String.format(pattern, "Source Files", source) + "\n"
                + String.format(pattern, "Test Files", testFiles));

    }

    private void check(ExecutionEnv env) {
        if (env.getServerFile() != null) {
            File serverFile = env.getServerFile();
            if (!serverFile.exists() || !serverFile.isFile()) {
                throw new SetupFailedException("Server file does not exists or is not a file: " + serverFile);
            }
        }

        checkFiles(env.getLibraryFiles(), "Library file doens't exist: {0}");
        checkFiles(env.getSourceFiles(), "Source file doens't exist: {0}");
        checkFiles(env.getTestFiles(), "Test file doens't exist: {0}");

    }

    private void checkFiles(List<File> files, String failMessage) {
        for (File file : files) {
            if (!file.exists()) {
                throw new SetupFailedException(String.format(failMessage, file.getPath()));
            }
        }
    }

    private MessageBus createMessageBus(ExecutionEnv env, TestReportModel model) {
        MessageBus messageBus = model.getMessageBus();
        if (env.isVerbose()) {
            MessageBus verboseMessageBus = MessageBusFactory.verbose();
            messageBus = MessageBusFactory.composite(verboseMessageBus, messageBus);
        }
        if (env.isRemote()) {
            messageBus = MessageBusFactory.composite(MessageBusFactory.remote(), messageBus);
        }

        return MessageBusFactory.sync(messageBus);
    }

    private void generateReports(TestReportModel model, TestConfig config, Coverage coverage, boolean createJUnitReport) throws IOException {
        if (config.getWorkingDirectory() != null) {
            ResourceHelper helper = Resources.createResourceHelper(config.getWorkingDirectory());
            ReportFactory.generateDefaultErrorReport(model, config);
            ReportFactory.generateHTMLCoverageReport(coverage, helper.newResourceHelper("coverage"));

            if (createJUnitReport) {
                ReportFactory.generateJUnitReport(model, config);
            }
        }
    }
}
