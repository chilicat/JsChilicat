package net.chilicat.testenv.server;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: dkuffner
 * Date: 10.08.2010
 * Time: 20:37:41
 * To change this template use File | Settings | File Templates.
 */
public abstract class ScriptResource {
    private final String name;

    public static ScriptResource createResource(final String name, final String resource) {
        return new ScriptResource(name) {
            @Override
            Reader getReader() throws FileNotFoundException {
                return new InputStreamReader(getClass().getResourceAsStream(resource));
            }
        };
    }

    public static ScriptResource createFile(final String name, final File file) {
        return new ScriptResource(name) {
            @Override
            public Reader getReader() throws FileNotFoundException {
                return new InputStreamReader(new FileInputStream(file));
            }
        };
    }

    protected ScriptResource(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract Reader getReader() throws FileNotFoundException;
}
