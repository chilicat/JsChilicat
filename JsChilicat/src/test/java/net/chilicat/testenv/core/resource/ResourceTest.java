package net.chilicat.testenv.core.resource;

import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class ResourceTest {
    @Test
    public void testResource() throws Exception {
        File tmp = File.createTempFile("ResourceTest", "tmp");
        File base = new File(tmp.getParent(), "/base");

        File child = new File(base, "child");
        Resource resource = new Resource(base, child);
        assertEquals("", resource.getPackage().last());
        assertEquals("", resource.getPackage().toString());

        File child2 = new File(child, "child2");
        resource = new Resource(base, child2);
        assertEquals("child", resource.getPackage().last());
        assertEquals("child", resource.getPackage().toString());

        File child3 = new File(child2, "child3");
        resource = new Resource(base, child3);
        assertEquals("child2", resource.getPackage().last());
        assertEquals("child.child2", resource.getPackage().toString());
    }
}
