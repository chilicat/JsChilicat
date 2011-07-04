package net.chilicat.testenv.utils;

import net.chilicat.testenv.core.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 */
public class JSLFileHandlerTest {
    @Test
    public void testRead() throws IOException {

        final File tmpFile = File.createTempFile("test", ".jsl");
        final InputStream res = getClass().getResourceAsStream("/net/chilicat/testenv/utils/test.jsl");
        Utils.transfer(res, true, new FileOutputStream(tmpFile), true);

        final JSLFileHandler jslFileHandler = new JSLFileHandler(tmpFile);
        final List<File> list = jslFileHandler.read();
        Assert.assertEquals(6, list.size());
        System.out.println(list);

    }
}
