package net.chilicat.testenv.core.resource;

import java.io.IOException;

/**
 * A finder is used to find another resource for a given resource.
 * For example it could find a name matching resource with different extension in the same directory.
 */
public interface ResourceFinder {
    /**
     * Find resource for given resource.
     *
     * @param resource the resource.
     * @return found resource or null.
     */
    public Resource find(Resource resource) throws IOException;
}
