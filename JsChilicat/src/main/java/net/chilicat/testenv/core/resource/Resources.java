package net.chilicat.testenv.core.resource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 */
public final class Resources {
    private Resources() {
        throw new AssertionError();
    }

    public static ResourceCollector createRecursiveDirectoryCollector(File dir, FileFilter filter) {
        return new RecursiveFileCollector(dir, filter);
    }


    public static ResourceHelper createResourceHelper(File baseDir) throws IOException {
        return new DefaultResourceHelper(baseDir);
    }
}
