package net.chilicat.testenv.core.resource;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: dkuffner
 * Date: 04.07.2010
 * Time: 19:36:08
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceHelper {
    /**
     * Returns a File with given name for resource in working directory.
     *
     * @param resource resources.
     * @param name     name.
     * @return a file.
     * @throws java.io.IOException io error.
     */
    public Resource location(Resource resource, String name) throws IOException;

    /**
     * Returns a File with resource name for resource in working directory.
     *
     * @param resource resources.
     * @return a file.
     * @throws java.io.IOException io error.
     */
    public Resource location(Resource resource) throws IOException;

    public Resource location(String path) throws IOException;

    public ResourceHelper newResourceHelper(String path) throws IOException;
}
