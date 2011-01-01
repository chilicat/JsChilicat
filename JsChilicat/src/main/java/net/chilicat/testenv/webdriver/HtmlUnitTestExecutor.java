package net.chilicat.testenv.webdriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 */
public class HtmlUnitTestExecutor extends AbstractWebDriverTestExecutor {
    @Override
    protected WebDriver createDriver() {
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3);
        driver.setJavascriptEnabled(true);
        
        return new WebDriverWrapper(driver);
    }


}
