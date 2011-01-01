package net.chilicat.testenv.rhino;

import net.chilicat.testenv.js.JsMessageBusInit;
import net.chilicat.testenv.util.MessageBus;
import org.mozilla.javascript.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class Defaults {
    public static String[] getNames() {
        return new String[]{"print", "load", "getProperty", "gc"};
    }

    /**
     * Get the global scope.
     * <p/>
     * <p>Walks the parent scope chain to find an object with a null
     * parent scope (the global object).
     *
     * @param obj a JavaScript object
     * @return the corresponding global scope
     */
    public static Scriptable getTopLevelScope(Scriptable obj) {
        for (; ;) {
            Scriptable parent = obj.getParentScope();
            if (parent == null) {
                return obj;
            }
            obj = parent;
        }
    }

    public static void gc(Context cx, Scriptable thisObj,
                          Object[] args, Function funObj) {

        System.gc();
    }

    /**
     * Load and execute a set of JavaScript source files.
     * <p/>
     * This method is defined as a JavaScript function.
     *
     * @param cx      context.
     * @param thisObj object.
     * @param args    args.
     * @param funObj  obj.
     */
    public static void load(Context cx, Scriptable thisObj,
                            Object[] args, Function funObj) {
        Scriptable shell = (Scriptable) getTopLevelScope(thisObj);
        for (Object arg : args) {
            processSource(cx, shell, Context.toString(arg));
        }
    }

    public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {

        MessageBus messageBus = JsMessageBusInit.getMessageBus(getTopLevelScope(thisObj));

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (i > 0)
                b.append(" ");

            // Convert the arbitrary JavaScript value into a string form.
            b.append(Context.toString(args[i]));
        }
        String out = b.toString();

        if ("\n".equals(out) || out.length() == 0) {
            return;
        }

        messageBus.log(b.toString());
    }

    public static String getProperty(Context cx, Scriptable thisObj,
                                     Object[] args, Function funObj) {
        return System.getProperty(Context.toString(args[0]));
    }

    /**
     * Evaluate JavaScript source.
     *
     * @param cx       the current context
     * @param scope    scope
     * @param filename the name of the file to compile, or null
     *                 for interactive mode.
     */
    private static void processSource(Context cx, Scriptable scope, String filename) {
        FileReader in = null;
        try {
            in = new FileReader(filename);
        }
        catch (FileNotFoundException ex) {
            Logger.getAnonymousLogger().severe("Couldn't open file \"" + filename + "\".");
            Context.reportError("Couldn't open file \"" + filename + "\".");
            return;
        }

        try {
            // Here we evalute the entire contents of the file as
            // a script. Text is printed only if the print() function
            // is called.
            cx.evaluateReader(scope, in, filename, 1, null);
        }
        catch (WrappedException we) {
            Logger.getAnonymousLogger().log(Level.SEVERE, String.format("Loading Script '%s' failed", filename), we);
            System.err.println(we.getWrappedException().toString());
            we.printStackTrace();
        }
        catch (EvaluatorException ee) {
            Logger.getAnonymousLogger().log(Level.SEVERE, String.format("Loading Script '%s' failed", filename), ee);
            System.err.println("js: " + ee.getMessage());
        }
        catch (JavaScriptException jse) {
            Logger.getAnonymousLogger().log(Level.SEVERE, String.format("Loading Script '%s' failed", filename), jse);
            System.err.println("js: " + jse.getMessage());
        }
        catch (IOException ioe) {
            Logger.getAnonymousLogger().log(Level.SEVERE, String.format("Loading Script '%s' failed", filename), ioe);
            System.err.println(ioe.toString());
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ioe) {
                Logger.getAnonymousLogger().log(Level.SEVERE, String.format("Close Script '%s' stream failed", filename), ioe);
                System.err.println(ioe.toString());
            }
        }
    }
}
