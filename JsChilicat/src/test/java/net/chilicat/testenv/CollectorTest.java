package net.chilicat.testenv;

import net.chilicat.testenv.core.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public class CollectorTest {
    @Test
    public void testEmpty() {

    }
    /*
    @Test
    public void testMain() throws Exception {
        File parent = unzipResource("/test_set_1.zip");
        //File parent = new File("D:\\dev\\gromit\\w1\\iNEWS\\Jennings\\middleware\\modules\\Authentication\\src\\");
        parent.deleteOnExit();

        Main.main(new String[]{
                "-src", new File(parent, "src").toString(),
                "-src-test", new File(parent, "test").toString(),
                "-remote",
                "-workingDir", new File(parent, "out").toString()
        });
    }
              */

    public static File unzipResource(String resource) throws IOException {
        InputStream in = CollectorTest.class.getResourceAsStream(resource);
        Assert.assertNotNull("Cannot find resource: " + resource, in);

        File parent = getDestinationFolder(resource);
        Utils.unzip(in, parent, true);

        return parent;
    }


    private static File getDestinationFolder(String resource) throws IOException {
        File file = File.createTempFile("testsetfiles", ".tmp");
        File parent = new File(file.getParentFile(), resource);

        file.delete();

        if (resource.endsWith(".zip")) {
            resource = resource.substring(0, resource.length() - ".zip".length());
        }

        for (int i = 0; parent.exists(); i++) {
            parent = new File(file.getParentFile(), resource + "_" + i);
        }

        Assert.assertTrue("Cannot create parent directory: " + parent, parent.mkdirs());

        return parent;
    }
}
