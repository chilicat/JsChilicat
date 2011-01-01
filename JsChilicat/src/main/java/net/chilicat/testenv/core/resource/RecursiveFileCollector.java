package net.chilicat.testenv.core.resource;

import net.chilicat.testenv.core.Factory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class RecursiveFileCollector implements ResourceCollector {
    private final File baseDir;
    private final FileFilter filter;

    public RecursiveFileCollector(File baseDir, FileFilter filter) {
        if (baseDir == null) {
            throw new NullPointerException("baseDir");
        }
        this.baseDir = baseDir;
        this.filter = filter;
    }

    public List<Resource> collectResources() {
        final List<Resource> result = new ArrayList<Resource>();
        for (File child : Factory.recursiveFileCollector(baseDir, filter)) {
            result.add(new Resource(baseDir, child));
        }
        return result;
    }

}
