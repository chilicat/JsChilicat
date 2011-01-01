package net.chilicat.testenv.server;

import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 */
public final class JsRouter extends ScriptableObject {
    private HttpServer server;
    private List<JsRestlet> restlets = new ArrayList<JsRestlet>();
    private List<FileResource> resources = new ArrayList<FileResource>();

    private final static Logger LOG = Logger.getLogger(JsRouter.class.getName());

    public JsRouter() {
    }

    public void setServer(HttpServer server) {
        if (server != this.server) {
            if (server != null) {
                for (JsRestlet restlet : restlets) {
                    LOG.finest("Attach: " + restlet.getAlias());
                    server.attachRestlet(restlet.getAlias(), restlet);
                }
                for (FileResource res : resources) {
                    LOG.finest("Attach Resource: " + res.getAlias() + " Directory: " + new File(res.getResource()).getAbsolutePath());
                    attachResource(server, res);
                }
            } else {
                for (JsRestlet restlet : restlets) {
                    LOG.finest("Dettach: " + restlet.getAlias());
                    this.server.detach(restlet.getAlias());
                }

                for (FileResource res : resources) {
                    LOG.finest("Dettach Resource: " + res.getAlias() + " Directory: " + new File(res.getResource()).getAbsolutePath());
                    detachResource(this.server, res);
                }
            }
            this.server = server;
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void jsFunction_attach(String alias, String resource) {
        FileResource res = new FileResource(alias, resource);
        resources.add(res);
        attachResource(server, res);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void jsFunction_attachRestlet(String alias, final ScriptableObject obj) {
        JsRestlet restlet = new JsRestlet(alias, obj);
        restlets.add(restlet);
        if (server != null) {
            server.attachRestlet(alias, restlet);
        }
    }

    @Override
    public String getClassName() {
        return "NativeRouter";
    }

    private void attachResource(HttpServer server, FileResource res) {
        if (server != null) {
            File file = new File(res.getResource());
            if (file.isDirectory()) {
                server.attachDir(res.getAlias(), file);
            }
        }
    }

    private void detachResource(HttpServer server, FileResource res) {
        if (server != null) {
            File file = new File(res.getResource());
            if (file.isDirectory()) {
                server.detach(file);
            }
        }
    }


    static class FileResource {
        private final String alias;
        private final String resource;

        FileResource(String alias, String resource) {
            this.alias = alias;
            this.resource = resource;
        }

        public String getAlias() {
            return alias;
        }

        public String getResource() {
            return resource;
        }
    }
}
