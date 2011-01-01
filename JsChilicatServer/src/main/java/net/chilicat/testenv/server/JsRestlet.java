package net.chilicat.testenv.server;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 */
public final class JsRestlet extends Restlet {
    private final ScriptableObject scope;
    private final String alias;

    public JsRestlet(String alias, ScriptableObject scope) {
        if (alias == null) {
            throw new NullPointerException("alias");
        }
        if (scope == null) {
            throw new NullPointerException("scope");
        }
        this.alias = alias;
        this.scope = scope;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public synchronized void handle(Request request, Response response) {
        final Context context = Context.enter();
        try {
            final JsRequest req = (JsRequest) context.newObject(scope, "NativeRequest");
            req.setRequest(request);

            final JsResponse res = (JsResponse) context.newObject(scope, "NativeResponse");
            res.setResponse(response);

            if (request.getMethod() == Method.GET) {
                final Function fn = getFunction("doGet");
                fn.call(context, scope, scope, new Object[]{req, res});
            } else if (request.getMethod() == Method.POST) {
                final Function fn = getFunction("doPost");
                fn.call(context, scope, scope, new Object[]{req, res});
            } else if (request.getMethod() == Method.DELETE) {
                final Function fn = getFunction("doDelete");
                fn.call(context, scope, scope, new Object[]{req, res});
            } else if (request.getMethod() == Method.PUT) {
                final Function fn = getFunction("doPut");
                fn.call(context, scope, scope, new Object[]{req, res});
            } else {
                throw new UnsupportedOperationException("Method not supported: " + request.getMethod().getName());
            }
            if (response.isEntityAvailable()) {
                response.setStatus(Status.SUCCESS_OK);
            } else {
                response.setStatus(Status.SUCCESS_NO_CONTENT);
            }
        } finally {
            Context.exit();
        }
    }

    public Function getFunction(String name) {
        final Object obj = ScriptableObject.getProperty(scope, name);
        if (obj instanceof Function) {
            return (Function) obj;
        }
        throw new UnsupportedOperationException("Method not supported: " + name);
    }
}
