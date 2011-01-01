package net.chilicat.testenv.rhino;

import org.mozilla.javascript.debug.DebuggableScript;


/**

 */
public interface LineVisitor {
    public void visit(DebuggableScript script, int lineNumber);
}
