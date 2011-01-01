package net.chilicat.testenv.server;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.restlet.Response;
import org.restlet.representation.StringRepresentation;

/**
 */
public final class JsResponse extends ScriptableObject {
    private Response response;

    @Override
    public String getClassName() {
        return "NativeResponse";
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void jsFunction_sendJSON(Object json) {
        String send = "{}";
        if (json instanceof String) {
            send = "['" + json + "']";
        } else if (json instanceof Number) {
            send = "[" + json + "]";
        } else if (json instanceof ScriptableObject) {
            send = toJSON((ScriptableObject) json);
        }
        response.setEntity(new StringRepresentation(send));
    }

    private String toJSON(ScriptableObject obj) {
        final Context cx = Context.enter();
        try {
            final ScriptableObject j = (ScriptableObject) ScriptableObject.getProperty(ScriptableObject.getTopLevelScope(this), "JSON");
            final Function fn = (Function) ScriptableObject.getProperty(j, "stringify");
            return (String) fn.call(cx, j, j, new Object[]{obj});
        } finally {
            Context.exit();
        }
    }
}
