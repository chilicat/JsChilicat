package net.chilicat.testenv.server;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 */
public interface ScriptInit {
    public void init(Context cx, ScriptableObject scope);
}
