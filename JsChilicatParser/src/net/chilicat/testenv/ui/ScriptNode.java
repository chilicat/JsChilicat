package net.chilicat.testenv.ui;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: dkuffner
 * Date: 10.08.2010
 * Time: 23:15:12
 * To change this template use File | Settings | File Templates.
 */
public class ScriptNode extends Node {
    private String script;
    private String path;

    public ScriptNode(String name, String script) {
        super(name);
        this.script = script;
        path = new File(script).getParent();
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", getUserObject(), path);
    }

    public String getScript() {
        return script;
    }

    @Override
    public boolean running() {
        for (int i = 0; i < getChildCount(); i++) {
            if (((Node) getChildAt(i)).running()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean done() {
        for (int i = 0; i < getChildCount(); i++) {
            if (!((Node) getChildAt(i)).done()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean passed() {
        for (int i = 0; i < getChildCount(); i++) {
            if (!((Node) getChildAt(i)).passed()) {
                return false;
            }
        }
        return true;
    }
}
