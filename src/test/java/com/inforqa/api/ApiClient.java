package com.inforqa.api;

import com.inforqa.core.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * The API equivalent of a Page Object: one place that knows how to talk to the API.
 * Every request is attached to the Allure report (request and response).
 */
public final class ApiClient {

    private ApiClient() {
    }

    private static RequestSpecification request() {
        return RestAssured.given()
            .baseUri(Config.apiBaseUrl())
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .filter(new AllureRestAssured());
    }

    public static Response get(String path) {
        return request().when().get(path);
    }

    public static Response post(String path, String jsonBody) {
        return request().body(jsonBody).when().post(path);
    }

    /** Update needs the login token, sent as a cookie. */
    public static Response put(String path, String jsonBody, String token) {
        return request().header("Cookie", "token=" + token).body(jsonBody).when().put(path);
    }

    /** Pass null as the token to try the call without logging in. */
    public static Response delete(String path, String token) {
        RequestSpecification spec = request();
        if (token != null) {
            spec = spec.header("Cookie", "token=" + token);
        }
        return spec.when().delete(path);
    }

    public static Response login(String username, String password) {
        String body = """
            {"username":"%s","password":"%s"}""".formatted(username, password);
        return post("/auth", body);
    }

    public static String adminToken() {
        return login(Config.apiUsername(), Config.apiPassword()).jsonPath().getString("token");
    }

    /** Builds the JSON for a booking so tests only mention what they care about. */
    public static String bookingJson(String firstName, String lastName, int totalPrice) {
        return """
            {"firstname":"%s","lastname":"%s","totalprice":%d,"depositpaid":true,
             "bookingdates":{"checkin":"2026-11-01","checkout":"2026-11-05"},
             "additionalneeds":"Breakfast"}""".formatted(firstName, lastName, totalPrice);
    }
}
