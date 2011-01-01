package net.chilicat.testenv.idea;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 */
public class TestEnvConfigurationType implements ConfigurationType {
    public String getDisplayName() {
        return "JsChilicat TestEnv";
    }

    public String getConfigurationTypeDescription() {
        return getDisplayName();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Icon getIcon() {

        return IconLoader.getIcon("/runConfigurations/junit.png");
    }

    @NotNull
    public String getId() {
        return "JsChilicatTestEnv";
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        ConfigurationFactory factory = new ConfigurationFactory(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new TestEnvConfiguration(getDisplayName(), project, this);
            }
        };

        return new ConfigurationFactory[]{factory};
    }
}
