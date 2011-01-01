package net.chilicat.testenv.server;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Parameter;

import java.util.Map;
import java.util.logging.Logger;

/**
 */
public final class JsRequest extends ScriptableObject {
    private Request request;

    private ScriptableObject json;
    private ScriptableObject attributes;
    private ScriptableObject header;

    private final static Logger LOG = Logger.getLogger(JsRequest.class.getName());

    @Override
    public String getClassName() {
        return "NativeRequest";
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public String jsFunction_getPath() {
        return request.getOriginalRef().toString();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public String jsFunction_getMethod() {
        return request.getMethod().getName();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ScriptableObject jsFunction_getHeader() {
        if (header == null) {
            Object value = request.getAttributes().get("org.restlet.http.headers");
            if (value instanceof Form) {
                final Context cx = Context.enter();
                try {
                    header = formToJSON(cx, (Form) value);
                } finally {
                    Context.exit();
                }
            }
        }
        return header;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ScriptableObject jsFunction_getAttributes() {
        if (attributes == null) {
            final Context cx = Context.enter();
            try {
                final ScriptableObject json = (ScriptableObject) cx.newObject(this);
                for (Map.Entry<String, Object> entry : request.getAttributes().entrySet()) {
                    if (entry.getKey().startsWith("org.restlet")) {
                        LOG.finest("Skip Restlet internal: " + entry.getKey());
                        continue;
                    }
                    if (entry.getValue() instanceof String || entry.getValue() instanceof Number || entry.getValue() instanceof Boolean) {
                        Object value = Context.javaToJS(entry.getValue(), json);
                        json.put(entry.getKey(), json, value);
                    } else {
                        if (entry.getValue() != null) {
                            LOG.finest("Uknown type " + entry.getValue().getClass() + "Skip: " + entry.getKey());
                        }
                    }
                }
                attributes = json;
            } finally {
                Context.exit();
            }
        }

        return attributes;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ScriptableObject jsFunction_getJSON() {
        if (json == null) {
            final Context cx = Context.enter();
            try {
                /*if (request.isEntityAvailable()) {
                    Form f = request.getEntityAsForm();
                    json = formToJSON(cx, f);
                } else {
                    Form f = request.getOriginalRef().getQueryAsForm();
                    json = formToJSON(cx, f);
                } */

                json = (ScriptableObject) cx.newObject(this);
                json.put("__text__", json, request.getEntityAsText());
                //System.out.println("Request: " + request.getEntityAsText()) ;

            } finally {
                Context.exit();
            }
        }
        return json;
    }

    private ScriptableObject formToJSON(Context cx, Form form) {
        ScriptableObject json = (ScriptableObject) cx.newObject(this);
        for (Parameter param : form) {
            json.put(param.getName(), json, param.getValue());
        }
        return json;
    }

    /*
    private ScriptableObject toJSON(String json) {
        final Context cx = Context.enter();
        try {
            final ScriptableObject j = (ScriptableObject) ScriptableObject.getProperty(ScriptableObject.getTopLevelScope(this), "JSON");
            final Function fn = (Function) ScriptableObject.getProperty(j, "parse");
            return (ScriptableObject) fn.call(cx, j, j, new Object[]{json});
        } finally {
            Context.exit();
        }
    } */
}
