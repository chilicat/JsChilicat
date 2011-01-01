package net.chilicat.testenv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Utils {
    private final static Map<String, File> MAP = new HashMap<String, File>();

    private Utils() {
        throw new AssertionError();
    }

    /**
     * Will convert the file into special browser (maybe Rhino specific) format.
     * Note: File.toURL().toString() returned url format will not work with rhino properly.     *
     *
     * @param file the file.
     * @return file as URL. Format: file:///[file.path].
     */
    public static String toURL(File file) {
        String uri = "file:///" + file.getAbsolutePath().replace("\\", "/");
        if (uri.endsWith("/")) {
            return uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    public static File getJSResource(String resource) throws IOException {
        String name = new File(resource).getName();
        if (resource.toLowerCase().endsWith(".js")) {
            name = name.substring(0, name.length() - ".js".length());
        }

        synchronized (MAP) {
            File file = MAP.get(resource);
            if (file == null) {
                file = createTemp(Utils.class.getResourceAsStream(resource), name);
                MAP.put(resource, file);
            }
            return file;
        }
    }

    static File createTemp(InputStream in, String name) throws IOException {
        File js = File.createTempFile(name, ".js");
        FileOutputStream out = new FileOutputStream(js);

        transfer(in, out);

        // TODO: In case of headless mode files should be deleted.
        // TODO: In case of Browser mode files should be created in a directory (not deleted).
        //js.deleteOnExit();
        return js;
    }

    public static void transfer(InputStream in, FileOutputStream out) throws IOException {
        transfer(in, true, out, true);
    }

    public static void transfer(InputStream in, boolean closeIn, FileOutputStream out, boolean closeOut) throws IOException {
        try {
            byte[] bytes = new byte[1024];
            int res = 0;
            while (res > -1) {
                res = in.read(bytes);
                if (res > 0) {
                    out.write(bytes, 0, res);
                }
            }
        } finally {
            try {
                if (closeOut) {
                    out.close();
                }
            } finally {
                if (closeIn) {
                    in.close();
                }
            }
        }
    }

    public static void unzip(InputStream in, File parent, boolean closeIn) throws IOException {

        ZipInputStream zipIn = new ZipInputStream(in);
        ZipEntry entry = zipIn.getNextEntry();
        try {
            while (entry != null) {
                if (entry.isDirectory()) {
                    File dir = new File(parent, entry.getName());
                    if (!dir.mkdirs()) {
                        throw new IOException("Cannot create directory: " + dir);
                    }
                } else {
                    File out = new File(parent, entry.getName());
                    Utils.transfer(zipIn, false, new FileOutputStream(out), true);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }


        } finally {
            try {
                zipIn.close();
            } finally {
                if (closeIn) {
                    in.close();
                }
            }
        }
    }
}
