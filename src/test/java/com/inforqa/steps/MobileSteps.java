package com.inforqa.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.inforqa.core.DriverFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.JavascriptExecutor;

/** Steps for checking the website on phone and tablet sizes (Chrome device emulation). */
public class MobileSteps {

    private Object runScript(String script) {
        return ((JavascriptExecutor) DriverFactory.getDriver()).executeScript(script);
    }

    @Given("I use a {string} device")
    public void iUseADevice(String deviceName) {
        DriverFactory.startMobile(deviceName);
    }

    @Then("the emulated screen width should be {int} pixels")
    public void theEmulatedScreenWidthShouldBe(int expected) {
        Number width = (Number) runScript("return window.screen.width;");
        assertThat(width.intValue()).as("emulated screen width").isEqualTo(expected);
    }

    @Then("the page should declare a mobile viewport")
    public void thePageShouldDeclareAMobileViewport() {
        String content = String.valueOf(runScript(
            "var m = document.querySelector('meta[name=\"viewport\"]');"
                + "return m ? m.getAttribute('content') : '';"));
        assertThat(content).as("viewport meta tag").contains("width=device-width");
    }

    @Then("the page should not scroll sideways")
    public void thePageShouldNotScrollSideways() {
        Number overflow = (Number) runScript(
            "return Math.max(document.documentElement.scrollWidth, document.body.scrollWidth)"
                + " - window.innerWidth;");
        assertThat(overflow.intValue()).as("pixels of sideways overflow").isLessThanOrEqualTo(5);
    }
}
