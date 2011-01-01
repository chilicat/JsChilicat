package net.chilicat.testenv.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;

/**
 */
class TestEnvDebugFrame implements DebugFrame {
    private final LineVisitor visitor;
    private final DebuggableScript script;

    public TestEnvDebugFrame(DebuggableScript script, LineVisitor visitor) {
        if (script == null) {
            throw new NullPointerException("script");
        }

        if (visitor == null) {
            throw new NullPointerException("visitor");
        }
        this.visitor = visitor;
        this.script = script;
    }

    public void onLineChange(Context context, int lineNumber) {
        visitor.visit(script, lineNumber);
    }


    public void onEnter(Context context, Scriptable scriptable, Scriptable scriptable1, Object[] objects) {

    }

    public void onExceptionThrown(Context context, Throwable throwable) {

    }

    public void onExit(Context context, boolean b, Object o) {

    }

    public void onDebuggerStatement(Context context) {

    }
}
