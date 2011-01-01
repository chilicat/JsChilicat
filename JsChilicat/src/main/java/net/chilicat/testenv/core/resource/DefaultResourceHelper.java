package net.chilicat.testenv.core.resource;

import java.io.File;
import java.io.IOException;

/**
 */
class DefaultResourceHelper implements ResourceHelper {
    public File baseFile;

    public DefaultResourceHelper(File baseFile) throws IOException {
        if (baseFile == null) {
            throw new NullPointerException("baseFile");
        }

        if (!baseFile.exists()) {
            if (!baseFile.mkdirs()) {
                throw new IOException("Cannot create base dir: " + baseFile);
            }
        } else if (!baseFile.isDirectory()) {
            throw new IOException("Base dir must be a directory.");
        }
        this.baseFile = baseFile;
    }

    public Resource location(Resource resource, String name) throws IOException {
        String path = resource.getPackage().toPath();
        File dir = new File(baseFile, path);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Cannot create directories: " + dir);
        }
        return new Resource(baseFile, new File(dir, name));
    }

    public Resource location(Resource resource) throws IOException {
        return location(resource, resource.getFile().getName());
    }

    public Resource location(String path) throws IOException {
        return new Resource(baseFile, new File(baseFile, path));
    }

    public ResourceHelper newResourceHelper(String path) throws IOException {
        File file = new File(baseFile, path);
        return new DefaultResourceHelper(file);
    }
}
