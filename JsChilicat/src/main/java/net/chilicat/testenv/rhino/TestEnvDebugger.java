package net.chilicat.testenv.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;

/**
 */
public class TestEnvDebugger implements Debugger {
    private final LineVisitor visitor;

    public TestEnvDebugger(LineVisitor visitor) {
        this.visitor = visitor;
    }

    public void handleCompilationDone(Context context, DebuggableScript debuggableScript, String s) {

    }

    public DebugFrame getFrame(final Context context, final DebuggableScript debuggableScript) {
        return new TestEnvDebugFrame(debuggableScript, visitor);
    }

}
