package net.chilicat.testenv.webdriver;

import org.openqa.selenium.chrome.ChromeDriver;

/**
 */
public class ChromeTestExecutor extends AbstractWebDriverTestExecutor {

    @Override
    protected WebDriver createDriver() {
        return new WebDriverWrapper(new ChromeDriver());
    }


}
