package net.chilicat.testenv.core.resource;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

/**
 */
public class ResourceHelperTest {
    @Test
    public void testResourceHelper() throws Exception {
        File file = File.createTempFile("ResourceHelperTest", "testResourceHelper");
        file = file.getParentFile();
        file.mkdirs();

        ResourceHelper helper = Resources.createResourceHelper(file);
        Resource res = helper.location("net/chilicat");

        Resource hello = helper.location(res, "HelloWorld.js");

        Assert.assertEquals("net.chilicate.HelloWorld", hello.getFQN());
        Assert.assertEquals("net/chilicate/HelloWorld", hello.relativePath());


    }
}
