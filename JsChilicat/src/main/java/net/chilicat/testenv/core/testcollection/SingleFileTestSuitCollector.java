package net.chilicat.testenv.core.testcollection;

import net.chilicat.testenv.core.DefaultTestSuite;
import net.chilicat.testenv.core.Factory;
import net.chilicat.testenv.core.SetupFailedException;
import net.chilicat.testenv.core.TestConfig;
import net.chilicat.testenv.core.resource.Resource;
import net.chilicat.testenv.core.resource.ResourceCollector;
import net.chilicat.testenv.core.resource.ResourceHelper;
import net.chilicat.testenv.core.resource.Resources;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class SingleFileTestSuitCollector implements TestCollector {

    public TestCollection collect(TestConfig config) {
        try {
            final FileFilter fileFilter = Factory.jsFilter();

            final List<Resource> resourceList = new ArrayList<Resource>();

            for (File file : config.getTestBase()) {
                if (file.isDirectory()) {
                    ResourceCollector collector = Resources.createRecursiveDirectoryCollector(file, fileFilter);
                    resourceList.addAll(collector.collectResources());
                } else if (fileFilter.accept(file)) {
                    ResourceHelper helper = Resources.createResourceHelper(file.getParentFile());
                    resourceList.add(helper.location(file.getName()));
                }
            }

            return toCollection(resourceList);

        } catch (IOException e) {
            throw new SetupFailedException(e);
        }
    }

    private TestCollection toCollection(List<Resource> resourceList) {
        final DefaultTestCollection collection = new DefaultTestCollection();
        for (Resource resource : resourceList) {
            DefaultTestSuite suit = new DefaultTestSuite(Factory.nullSetup(), Factory.nullServer(), resource.getPackage().toString());
            suit.add(resource.getFile());
            collection.add(suit);
        }

        return collection;
    }
}
