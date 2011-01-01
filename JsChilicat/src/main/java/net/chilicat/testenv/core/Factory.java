package net.chilicat.testenv.core;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 */
public final class Factory {
    private Factory() {
        throw new AssertionError();
    }

    public static FileFilter jsFilter() {
        return JS_FILTER;
    }

    public static TestServer nullServer() {
        return NULL_SERVER;
    }

    public static TestSetup nullSetup() {
        return NULL_SETUP;
    }

    public static List<File> recursiveFileCollector(File base, FileFilter filter) {
        List<File> files = new ArrayList<File>();
        collect(base, files, filter);
        return files;
    }

    private static void collect(File file, List<File> files, FileFilter filter) {
        if (file.isDirectory()) {

            List<File> dirs = new ArrayList<File>();

            for (File child : file.listFiles()) {
                if (child.isDirectory()) {
                    dirs.add(child);
                } else if (filter.accept(child)) {
                    files.add(child);
                }
            }

            for (File dir : dirs) {
                collect(dir, files, filter);
            }
        } else {
            if (filter.accept(file)) {
                files.add(file);
            }
        }
    }


    private static final TestServer NULL_SERVER = new TestServer() {
        public void start() {

        }

        public void stop() {

        }
    };

    private static final TestFilter DEFAULT_FILTER = new TestFilter() {
        public boolean accept(String fileName) {
            return fileName.endsWith(".js");
        }
    };

    private static final TestSetup NULL_SETUP = new TestSetup() {
        public TestFilter getFilter() {
            return DEFAULT_FILTER;
        }
    };

    private static final FileFilter JS_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".js");
        }
    };

}
