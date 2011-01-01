package net.chilicat.testenv.js;

import net.chilicat.testenv.core.SetupFailedException;
import net.chilicat.testenv.rhino.RhinoTestExecutor;
import net.chilicat.testenv.util.MessageBus;
import net.chilicat.testenv.util.MessageBusFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
@SuppressWarnings({"UnusedDeclaration"})
public class JsMessageBusInit extends org.mozilla.javascript.ScriptableObject {
    private MessageBus messageBus = MessageBusFactory.nullBus();

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public void setMessageBus(MessageBus messageBus) {
        if (messageBus == null) {
            throw new NullPointerException("messageBuss");
        }
        this.messageBus = messageBus;
    }

    public void jsFunction_testAdded(String testName) {
        messageBus.testAdded(testName);
    }

    public void jsFunction_moduleAdded(String moduleName) {
        messageBus.moduleAdded(moduleName);
    }

    public void jsFunction_testStarted(String started) {
        messageBus.testStarted(started);
    }

    public void jsFunction_testPassed(String testName) {
        messageBus.testPassed(testName);
    }

    public void jsFunction_testFailed(String testName, String errorMessage) {
        messageBus.testFailed(testName, errorMessage);
    }

    public void jsFunction_moduleStart(String moduleName) {
        messageBus.moduleStart(moduleName);
    }

    public void jsFunction_moduleDone(String moduleName) {
        messageBus.moduleDone(moduleName);
    }

    public void jsFunction_testSuitStart(String packageName) {
        messageBus.testSuitStart(packageName);
    }

    public void jsFunction_testScriptStart(String scriptName) {
        messageBus.testScriptStart(scriptName);
    }

    public void jsFunction_testScriptDone(String scriptName) {
        messageBus.testScriptDone(scriptName);
    }

    public void jsFunction_testSuitDone(String packageName) {
        messageBus.testScriptDone(packageName);
    }

    public void jsFunction_log(String message) {
        messageBus.log(message);
    }

    @Override
    public String getClassName() {
        return "MessageBus";
    }

    public static MessageBus getMessageBus(Scriptable scope) {
        ScriptableObject obj = (ScriptableObject) scope.get("JsChilicat", scope);
        if (obj != null) {
            Object cat = obj.get("bus", scope);
            if (cat instanceof JsMessageBusInit) {
                return ((JsMessageBusInit) cat).getMessageBus();
            }
        }
        return MessageBusFactory.nullBus();
    }

    public static void createInScope(Context cx, ScriptableObject scope, MessageBus bus) {
        try {
            ScriptableObject.defineClass(scope, JsMessageBusInit.class, true);
            InputStream in = JsMessageBusInit.class.getResourceAsStream("/lib/jschilicat.js");
            InputStreamReader reader = new InputStreamReader(in);
            try {
                Script s = RhinoTestExecutor.compileScript(cx, reader, "jschilicat.js");
                s.exec(cx, scope);
                ScriptableObject obj = (ScriptableObject) scope.get("JsChilicat", scope);
                Object cat = obj.get("bus", scope);
                ((JsMessageBusInit) cat).setMessageBus(bus);
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            throw new SetupFailedException("Cannot setup JsChilicat", e);
        }
    }
}
