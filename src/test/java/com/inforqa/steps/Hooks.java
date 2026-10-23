package com.inforqa.steps;

import com.inforqa.core.Config;
import com.inforqa.core.DriverFactory;
import com.inforqa.core.VideoRecorder;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Runs automatically before and after the whole run, and around every browser (@ui) scenario.
 * API (@api) scenarios do not need a browser, so they skip the browser hooks.
 * @mobile scenarios start their own emulated device in a step, so they skip the desktop start-up.
 */
public class Hooks {

    /** Once per run: writes the "Environment" box shown on the Allure report home page. */
    @BeforeAll
    public static void writeEnvironmentInfo() {
        Properties info = new Properties();
        info.setProperty("Website", Config.baseUrl());
        info.setProperty("Browser", Config.browser());
        info.setProperty("Headless", String.valueOf(Config.headless()));
        info.setProperty("Java", System.getProperty("java.version"));
        info.setProperty("OS", System.getProperty("os.name"));
        try {
            Path folder = Path.of("target", "allure-results");
            Files.createDirectories(folder);
            try (OutputStream out = Files.newOutputStream(folder.resolve("environment.properties"))) {
                info.store(out, null);
            }
            // Lets the Allure report group failures into categories (Phase 9).
            try (InputStream categories = Hooks.class.getClassLoader().getResourceAsStream("allure-categories.json")) {
                if (categories != null) {
                    Files.copy(categories, folder.resolve("categories.json"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not write Allure environment info: " + e.getMessage());
        }
    }

    @Before("@ui and not @mobile")
    public void setUp() {
        DriverFactory.start();
        VideoRecorder.start();
    }

    @AfterStep("@ui")
    public void afterEachStep(Scenario scenario) {
        if (Config.stepScreenshots()) {
            attachScreenshot(scenario, "Step screenshot");
        }
    }

    @After("@ui")
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            attachScreenshot(scenario, "Screenshot on failure");
        }
        VideoRecorder.stop(scenario.getName() + "-line" + scenario.getLine());
        DriverFactory.quit();
    }

    private static void attachScreenshot(Scenario scenario, String name) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", name);
        }
    }
}
