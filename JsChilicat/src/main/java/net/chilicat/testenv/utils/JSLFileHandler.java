package net.chilicat.testenv.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 */
public class JSLFileHandler {
    private final File file;

    public JSLFileHandler(File file) {
        this.file = file;
    }


    public List<File> read() throws IOException {
        final File parent = file.getParentFile();
        final Scanner scanner = new Scanner(new FileInputStream(file));
        final List<File> paths = new ArrayList<File>();
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                for (String name : line.split(";")) {
                    name = name.trim();
                    if (name.length() > 0) {
                        final File source = new File(parent, name);
                        paths.add(source);
                    }
                }
            }
        } finally {
            scanner.close();
        }
        return paths;
    }
}
