package net.chilicat.testenv.core;

import net.chilicat.testenv.server.HttpServer;
import net.chilicat.testenv.server.JsServer;
import net.chilicat.testenv.server.ScriptInit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author dkuffner
 */
public class DefaultTestServer implements TestServer {
    private final File serverFile;
    private final String serverResource;
    private final HttpServer server;
    private final Map<String, String> context;

    private List<ScriptInit> scripts = new ArrayList<ScriptInit>();

    private JsServer jsserver;

    public DefaultTestServer(HttpServer server, File serverFile, Map<String, String> context) {
        this.server = server;
        this.serverFile = serverFile;
        this.serverResource = null;
        this.context = Collections.unmodifiableMap(new HashMap<String, String>(context));
    }

    public DefaultTestServer(HttpServer server, String serverResource, Map<String, String> context) {
        this.server = server;
        this.serverResource = serverResource;
        this.serverFile = null;
        this.context = Collections.unmodifiableMap(new HashMap<String, String>(context));
    }

    public synchronized void start() {
        if (jsserver != null) {
            throw new AssertionError("start called twice");
        }
        try {
            jsserver = serverFile != null ? JsServer.create(serverFile) : JsServer.create(serverResource);
            jsserver.setContext(context);
            for (ScriptInit sc : scripts) {
                jsserver.init(sc);
            }
            jsserver.start(server);
        } catch (FileNotFoundException e) {
            throw new SetupFailedException(e);
        }
    }

    public synchronized void stop() {
        if (jsserver != null) {
            jsserver.dispose();
            jsserver = null;
        }
    }

    public void init(ScriptInit scriptInit) {
        scripts.add(scriptInit);
    }
}
