package net.chilicat.testenv.core.resource;

import java.io.File;
import java.io.IOException;

/**
 */
public class HtmlFileMatchFinder implements ResourceFinder {

    public Resource find(Resource resource) {
        File file = resource.getFile();
        File base = file.getParentFile();
        String name = file.getName();
        int index = name.lastIndexOf('.');
        if (index > 0) {
            name = name.substring(0, index);
        }

        File newFile = new File(base, name + ".html");

        if (!newFile.exists()) {
            newFile = new File(base, "default.html");
        }

        if (newFile.exists()) {
            return new Resource(base, newFile);
        }

        return null;
    }
}
