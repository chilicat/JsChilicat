package net.chilicat.testenv.core.testcollection;

import net.chilicat.testenv.core.Factory;
import net.chilicat.testenv.core.TestConfig;
import net.chilicat.testenv.core.TestServer;
import net.chilicat.testenv.core.TestSetup;
import net.chilicat.testenv.server.HttpServer;
import net.chilicat.testenv.server.JsServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;

/**
 */
public class DefaultTestCollector extends AbstractTestCollector {

    @Override
    protected TestServer initTestServer(final File dir, final TestConfig config) {
        final File file = new File(dir, "server.jss");
        if (!file.exists()) {
            return newTestServer(config.getServer(), dir);
        } else {
            return new TestServer() {
                private JsServer server;

                public void start() {
                    try {
                        server = JsServer.create(file);
                        server.setContext(Collections.singletonMap("baseDir", dir.getAbsolutePath()));
                        server.start(config.getServer());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

                public void stop() {
                    server.dispose();
                }
            };
        }
    }


    /**
     * This test server will map the current base directory has root path.
     * That will able tests to request resources via Ajax.
     *
     * @param base the base.
     * @return A TestServer.
     */
    private TestServer newTestServer(final HttpServer server, final File base) {
        return new TestServer() {
            public void start() {
                server.attachDir("/", base);
            }

            public void stop() {
                //comp.getDefaultHost().detach(router);
                server.detach(base);
            }
        };
    }


    @Override
    protected TestSetup initSetup(File dir) {
        return Factory.nullSetup();
    }

}
