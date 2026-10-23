package com.inforqa.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.inforqa.api.ApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;

/** Plain-English API steps. The shared ApiContext is handed in automatically by PicoContainer. */
public class ApiSteps {

    private final ApiContext ctx;

    public ApiSteps(ApiContext ctx) {
        this.ctx = ctx;
    }

    // ---------- generic steps ----------

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String path) {
        ctx.response = ApiClient.get(path);
    }

    @When("I send a GET request to an address that does not exist")
    public void iSendAGetRequestToAnAddressThatDoesNotExist() {
        // The .invalid ending is reserved, so this can never reach a real website.
        ctx.response = ApiClient.getAbsolute("https://this-host-does-not-exist.invalid/ping");
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expected) {
        assertThat(ctx.response.getStatusCode()).as("HTTP status code").isEqualTo(expected);
    }

    @Then("the response field {string} should equal {string}")
    public void theResponseFieldShouldEqual(String fieldPath, String expected) {
        assertThat(ctx.response.jsonPath().getString(fieldPath)).as("field " + fieldPath).isEqualTo(expected);
    }

    // ---------- bookings ----------

    @Then("the response should contain a list of bookings")
    public void theResponseShouldContainAListOfBookings() {
        List<Object> ids = ctx.response.jsonPath().getList("bookingid");
        assertThat(ids).as("list of booking ids").isNotEmpty();
    }

    @Given("a new booking exists for {string} {string} with total price {int}")
    public void aNewBookingExistsFor(String first, String last, int price) {
        createBooking(first, last, price);
    }

    @When("I create a booking for {string} {string} with total price {int}")
    public void iCreateABookingFor(String first, String last, int price) {
        createBooking(first, last, price);
    }

    @Then("the response should contain a booking id")
    public void theResponseShouldContainABookingId() {
        assertThat(ctx.bookingId).as("booking id").isNotNull().isPositive();
    }

    @When("I request the booking I created")
    public void iRequestTheBookingICreated() {
        ctx.response = ApiClient.get("/booking/" + ctx.bookingId);
    }

    @When("I request booking number {int}")
    public void iRequestBookingNumber(int id) {
        ctx.response = ApiClient.get("/booking/" + id);
    }

    @Given("I am logged in as an admin")
    public void iAmLoggedInAsAnAdmin() {
        ctx.token = ApiClient.adminToken();
        assertThat(ctx.token).as("login token").isNotBlank();
    }

    @When("I log in with username {string} and password {string}")
    public void iLogInWith(String username, String password) {
        ctx.response = ApiClient.login(username, password);
    }

    @When("I update the booking I created to total price {int}")
    public void iUpdateTheBooking(int newPrice) {
        String body = ApiClient.bookingJson(ctx.firstName, ctx.lastName, newPrice);
        ctx.response = ApiClient.put("/booking/" + ctx.bookingId, body, ctx.token);
    }

    @When("I delete the booking I created")
    public void iDeleteTheBooking() {
        ctx.response = ApiClient.delete("/booking/" + ctx.bookingId, ctx.token);
    }

    @When("I try to delete the booking I created without logging in")
    public void iTryToDeleteWithoutLoggingIn() {
        ctx.response = ApiClient.delete("/booking/" + ctx.bookingId, null);
    }

    @Then("the booking I created should no longer exist")
    public void theBookingShouldNoLongerExist() {
        ctx.response = ApiClient.get("/booking/" + ctx.bookingId);
        assertThat(ctx.response.getStatusCode()).as("HTTP status after delete").isEqualTo(404);
    }

    private void createBooking(String first, String last, int price) {
        ctx.firstName = first;
        ctx.lastName = last;
        ctx.response = ApiClient.post("/booking", ApiClient.bookingJson(first, last, price));
        if (ctx.response.getStatusCode() == 200) {
            ctx.bookingId = ctx.response.jsonPath().getInt("bookingid");
        }
    }
}
