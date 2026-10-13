package com.inforqa.core;

import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates and closes the browser. ThreadLocal keeps each test's browser
 * separate, which allows running tests in parallel.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    /** Normal desktop browser. */
    public static void start() {
        WebDriver driver;
        if (Config.browser().equals("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            if (Config.headless()) {
                options.addArguments("-headless");
            }
            driver = new FirefoxDriver(options);
        } else {
            driver = new ChromeDriver(chromeOptions(false));
        }
        DRIVER.set(driver);
    }

    /** Chrome pretending to be a phone or tablet (screen size and browser identity). */
    public static void startMobile(String deviceName) {
        quit();
        MobileDevice device = MobileDevice.find(deviceName);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("width", device.width());
        metrics.put("height", device.height());
        metrics.put("pixelRatio", device.pixelRatio());

        Map<String, Object> emulation = new HashMap<>();
        emulation.put("deviceMetrics", metrics);
        emulation.put("userAgent", device.userAgent());

        ChromeOptions options = chromeOptions(true);
        options.setExperimentalOption("mobileEmulation", emulation);
        DRIVER.set(new ChromeDriver(options));
    }

    private static ChromeOptions chromeOptions(boolean mobile) {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        if (!mobile) {
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
