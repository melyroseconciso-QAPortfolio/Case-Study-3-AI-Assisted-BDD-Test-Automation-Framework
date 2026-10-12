package com.inforqa.steps;

import io.restassured.response.Response;

/** Holds what one API scenario needs to remember between steps. A fresh copy is made for each scenario. */
public class ApiContext {
    public Response response;
    public String token;
    public Integer bookingId;
    public String firstName;
    public String lastName;
}
