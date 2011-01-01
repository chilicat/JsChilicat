package net.chilicat.testenv.server;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 */
public final class JsServer {
    private Map<String, String> context;
    private final List<ScriptInit> initScripts = new ArrayList<ScriptInit>();

    public static JsServer create(File file) throws FileNotFoundException {
        return new JsServer(ScriptResource.createFile(file.getAbsolutePath(), file));
    }

    public static JsServer create(String resource) {
        return new JsServer(ScriptResource.createResource("<jar>" + resource, resource));
    }

    private JsRouter router;
    private final ScriptableObject scope;

    JsServer(ScriptResource resource) throws SetupFailedException {
        if (resource == null) {
            throw new NullPointerException("resource");
        }

        try {
            final Context cx = Context.enter();
            scope = cx.initStandardObjects();

            ScriptableObject.defineClass(scope, JsRouter.class);
            ScriptableObject.defineClass(scope, JsRequest.class);
            ScriptableObject.defineClass(scope, JsResponse.class);

            loadScript(cx, scope, "/lib/json2.js");
            loadScript(cx, scope, "/lib/console.js");
            loadScript(cx, scope, "/lib/jschilicatServer.js");

            Reader reader = resource.getReader();
            try {
                final Script script = cx.compileReader(reader, resource.getName(), 1, null);
                script.exec(cx, scope);
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            throw new SetupFailedException(e);
        }
    }
    /*
    public void defineMethods(Class<?> clazz, String[] methods) {
        scope.defineFunctionProperties(methods, clazz, ScriptableObject.DONTENUM);
    } */

    private void startChilicatServer(Context cx, ScriptableObject scope) {
        ScriptableObject jsChilicat = (ScriptableObject) scope.get("JsChilicat", scope);
        Function startChilicat = (Function) jsChilicat.get("startServer", scope);
        startChilicat.call(cx, jsChilicat, jsChilicat, new Object[]{toMapToJS(context, scope)});
    }

    private ScriptableObject toMapToJS(Map<String, String> map, ScriptableObject scope) {
        final Context cx = Context.enter();
        try {
            final ScriptableObject json = (ScriptableObject) cx.newObject(scope);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object value = Context.javaToJS(entry.getValue(), json);
                json.put(entry.getKey(), json, value);
            }
            return json;
        } finally {
            Context.exit();
        }
    }

    public void start(HttpServer server) {
        if (server != null) {
            Context cx = Context.enter();
            try {
                for (ScriptInit i : initScripts) {
                    i.init(cx, scope);
                }

                startChilicatServer(cx, scope);
                //cx.evaluateString(scope, "JsChilicat.startServer({});", "start servers", 1, null);
                router = (JsRouter) cx.evaluateString(scope, "JsChilicat.router;", "Get Router", 1, null);
            } finally {
                Context.exit();
            }
        }
        router.setServer(server);

    }

    public static void main(String[] args) {
        final HttpServer server = HttpServer.newServer(-1);
        try {
            JsServer jss = JsServer.create("router-test.js");
            jss.setContext(Collections.singletonMap("baseDir", "JsChilicatServer/src/main/java/net/chilicat/testenv/server"));
            jss.start(server);
        } catch (Exception e) {
            server.dispose();
            e.printStackTrace();
        }
    }

    private static void loadScript(Context cx, ScriptableObject scope, String resource) throws IOException {
        final InputStream stream = JsServer.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new SetupFailedException("Resource could not be found: " + resource);
        }
        try {
            final Script script = cx.compileReader(new InputStreamReader(stream), resource, 1, null);
            script.exec(cx, scope);
        } finally {
            stream.close();
        }
    }

    public void dispose() {
        start(null);
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }


    public void init(ScriptInit scriptInit) {
        initScripts.add(scriptInit);
    }
}

