package net.chilicat.testenv.core.resource;

import java.io.InputStream;

/**
 */
public interface Writer {
    public void text(String string);

    public void transfer(InputStream in);
}
