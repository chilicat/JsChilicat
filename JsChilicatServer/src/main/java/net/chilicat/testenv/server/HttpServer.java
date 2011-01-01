package net.chilicat.testenv.server;

import net.chilicat.testenv.Utils;
import org.restlet.*;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.ReaderRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Directory;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.BindException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public final class HttpServer {
    public static HttpServer newServer(int port) {
        // Create a component
        try {
            Component comp = null;

            boolean failIfPortBusy = port != -1;
            port = failIfPortBusy ? port : 8182;
            for (; comp == null; port++) {
                comp = setup(port, failIfPortBusy);
            }
            return new HttpServer(port - 1, comp);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Component setup(int port, boolean fail) throws Exception {
        try {
            final Component component = new Component();
            component.getServers().add(Protocol.HTTP, port);
            component.getClients().add(Protocol.FILE);

            component.start();
            return component;
        } catch (BindException ex) {
            if (fail) {
                throw new SetupFailedException(String.format("Port (%s) seems to be in use.", port), ex);
            }

            Logger.getAnonymousLogger().log(Level.INFO, String.format("Port (%s) seems to be in use. try next...", port));
            return null;
        }

    }

    private final Component comp;
    private final Map<Object, Restlet> restlets = new HashMap<Object, Restlet>();
    private final int port;

    HttpServer(int port, Component comp) {
        if (comp == null) {
            throw new NullPointerException("comp");
        }
        this.comp = comp;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void dispose() {
        try {
            comp.stop();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Cannot shutdown http server", e);
        }
    }

    public void detach(final String resourcePath) {
        detachImpl(resourcePath);
    }

    public void attachResource(String path, final String resourcePath) {
        if (restlets.containsKey(resourcePath)) {
            return;
        }

        final Restlet restlet = new ResourceRestlet(resourcePath);
        comp.getDefaultHost().attach(path, restlet);
        restlets.put(resourcePath, restlet);
    }

    public void detach(final Representation representation) {
        detachImpl(representation);
    }

    public void attachRestlet(final String path, final Restlet restlet) {
        comp.getDefaultHost().attach(path, restlet);
    }

    public void attachFile(final String path, final File file) {
        if (restlets.containsKey(path)) {
            return;
        }

        final Restlet restlet = new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                MediaType type = mimeTypeForName(file.getName());
                
                response.setEntity(new FileRepresentation(file, type));
                response.setStatus(Status.SUCCESS_OK);
                
            }
        };

        comp.getDefaultHost().attach(path, restlet);
        restlets.put(path, restlet);
    }

    private static MediaType mimeTypeForName(String name) {
        MediaType type = MediaType.TEXT_HTML;
        if(name.endsWith(".js")) {
            type = MediaType.valueOf("application/javascript");
        } else if(name.endsWith(".html") || name.endsWith(".htm")) {
            type = MediaType.TEXT_HTML;
        }
        return type;
    }

    public void attachStringContent(final String path, final String content) {
        if (restlets.containsKey(path)) {
            return;
        }

        final Restlet restlet = new Restlet() {
            @Override
            public void handle(Request request, Response response) {
                response.setEntity(new ReaderRepresentation(new StringReader(content), mimeTypeForName(path)));
                response.setStatus(Status.SUCCESS_OK);
            }
        };

        comp.getDefaultHost().attach(path, restlet);
        restlets.put(path, restlet);
    }

    public void attachDir(final String path, final File directory) {
        if (restlets.containsKey(directory)) {
            return;
        }

        final Application application = new Application() {
            @Override
            public Restlet createInboundRoot() {
                Directory d = new Directory(getContext(), Utils.toURL(directory));
                d.setDeeplyAccessible(true);
                d.setListingAllowed(true);
                return d;
            }
        };
        comp.getDefaultHost().attach(path, application);

        restlets.put(directory, comp);
    }

    public void detach(File directory) {
        detachImpl(directory);
    }

    private void detachImpl(Object obj) {
        final Restlet restlet = restlets.remove(obj);
        if (restlet != null) {
            comp.getDefaultHost().detach(restlet);
        }
    }


    private class ResourceRestlet extends Restlet {
        private final String resourcePath;

        public ResourceRestlet(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        @Override
        public void handle(Request request, Response response) {
            final InputStream stream = getClass().getResourceAsStream(resourcePath);
            response.setEntity(new InputRepresentation(stream, mimeTypeForName(resourcePath)));
            response.setStatus(Status.SUCCESS_OK);
        }
    }
}
