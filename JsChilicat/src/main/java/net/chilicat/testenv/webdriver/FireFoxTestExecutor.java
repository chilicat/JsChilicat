package net.chilicat.testenv.webdriver;

import net.chilicat.testenv.ExecutionEnv;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

/**
 */
public class FireFoxTestExecutor extends AbstractWebDriverTestExecutor {
    private final ExecutionEnv env;

    public FireFoxTestExecutor(ExecutionEnv env) {
        this.env = env;
    }

    @Override
    protected WebDriver createDriver() {
        File file = env.getFirefoxProfile();
        FirefoxProfile p;
        if (file != null) {
            p = new FirefoxProfile(new File("C:\\Users\\dkuffner\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\test"));
        } else {
            p = new FirefoxProfile();
        }
        return new WebDriverWrapper(new FirefoxDriver(p));
    }
}
