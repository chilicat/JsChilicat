package net.chilicat.testenv.core.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: dkuffner
 * Date: 04.07.2010
 * Time: 19:15:35
 * To change this template use File | Settings | File Templates.
 */
public class Package {
    private final List<String> list;

    public static Package fromFiles(File baseDir, File child) {
        int offset = baseDir.getAbsolutePath().length();
        String pkg = child.getAbsolutePath().substring(offset);
        return fromString(pkg, !child.isDirectory());
    }

    public static Package fromString(String path, boolean removeLast) {
        List<String> list = toList(path);

        if (removeLast && list.size() > 0) {
            list = list.subList(0, list.size() - 1);
        }

        return new Package(list);
    }

    private static List<String> toList(String path) {
        path = path.replace("\\.", "/");
        path = path.replace("\\", "/");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return Arrays.asList(path.split("/"));
    }

    Package(List<String> list) {
        this.list = Collections.unmodifiableList(list);
    }

    public String toString() {
        return toString('.');
    }

    public String toString(char separator) {
        StringBuffer b = new StringBuffer();
        for (String s : list) {
            if (b.length() != 0) {
                b.append(separator);
            }
            b.append(s);
        }
        return b.toString();
    }

    public String last() {
        if (list.isEmpty()) {
            return "";
        }
        return list.get(list.size() - 1);
    }

    public Package append(String path) {
        List<String> res = new ArrayList<String>(list);
        res.addAll(toList(path));
        return new Package(res);
    }

    public String toPath() {
        return toString('/');
    }

    public int tokenCount() {
        return list.size();
    }
}
