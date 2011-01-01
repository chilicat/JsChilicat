package net.chilicat.testenv.core.resource;

import net.chilicat.testenv.core.Utils;

import java.io.*;

/**
 */
public class Resource {
    private final File baseDir;
    private final File file;
    private final Package pkg;

    Resource(File baseDir, File file) {
        if (baseDir == null) {
            throw new NullPointerException("baseDir");
        }
        if (file == null) {
            throw new NullPointerException("file");
        }

        assert file.getAbsolutePath().startsWith(baseDir.getAbsolutePath());
        this.baseDir = baseDir;
        this.file = file;
        this.pkg = Package.fromFiles(baseDir, file);
    }

    public void write(InputStream in) throws IOException {
        if (in == null) {
            throw new NullPointerException("in"); //NonNls
        }

        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Cannot create directories: " + file);
        }
        FileOutputStream out = new FileOutputStream(file);
        Utils.transfer(in, out);
    }

    public String readAsText() throws IOException {
        FileInputStream in = new FileInputStream(file);
        return Utils.readAsText(in); // util will close stream.
    }

    public void writeAsText(String string) throws IOException {
        Utils.writeAsText(file, string);
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getFile() {
        return file;
    }

    public Package getPackage() {
        return pkg;
    }

    public String getFQN() {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if (index > -1) {
            name = name.substring(0, index);
        }

        String res = pkg.toString();
        if (res.length() > 0) {
            res += ".";
        }
        res += name;
        return res;
    }

    /*public String relativeTo(Resource htmlFile) {
        int length = getPackage().tokenCount();
        StringBuffer buffer = new StringBuffer(length*5);
        
        for(int i=0; i<length; i++) {

        }
    } */

    public String relativePath() {
        String name = file.getName();

        String res = pkg.toPath();
        if (res.length() > 0) {
            res += "/";
        }
        res += name;
        return res;
    }
}
