package net.chilicat.testenv.webdriver;

import org.openqa.selenium.firefox.FirefoxDriver;

/**
 */
public class FireFoxTestExecutor extends AbstractWebDriverTestExecutor {
    @Override
    protected WebDriver createDriver() {
        return new WebDriverWrapper(new FirefoxDriver());
    }
}
