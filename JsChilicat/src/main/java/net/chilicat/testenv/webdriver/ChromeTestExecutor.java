package net.chilicat.testenv.webdriver;

import net.chilicat.testenv.core.Utils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 */
public class ChromeTestExecutor extends AbstractWebDriverTestExecutor {

    @Override
    protected WebDriver createDriver() {
        try {
            final File driver = getDriverFile();
            final ChromeDriverService.Builder builder = new ChromeDriverService.Builder();
            builder.usingChromeDriverExecutable(driver);

            final ChromeDriverService build = builder.build();

            return new WebDriverWrapper(new ChromeDriver(build), new Runnable() {
                public void run() {
                    build.stop();
                    if (!driver.delete()) {
                        Logger.getAnonymousLogger().warning("Couldn't delete chrome driver: " + driver);
                    }
                }
            });
        } catch (IOException e) {
            throw new Error(e);
        }

    }

    private File getDriverFile() throws IOException {
        final File tmpFolder = new File(System.getProperty("java.io.tmpdir"));
        final File tempFile;
        final String resource;
        if (Utils.isWindows()) {
            resource = "/executables/chromedriver.exe";
            tempFile = new File(tmpFolder, "js_chromedriver.exe");
        } else if (Utils.isMac()) {
            resource = "/executables/chromedriver";
            tempFile = new File(tmpFolder, "js_chromedriver");
        } else {
            throw new Error("Unsupported OS");
        }

        if (!tempFile.exists()) {
            final InputStream in = this.getClass().getResourceAsStream(resource);
            Utils.transfer(in, true, new FileOutputStream(tempFile), true);
        }
        return tempFile;
    }


}
