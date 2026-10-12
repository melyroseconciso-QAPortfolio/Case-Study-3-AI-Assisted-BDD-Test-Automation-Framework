@api
Feature: Booking API checks
  As a QA engineer
  I want to confirm the booking service behaves correctly
  So that problems are caught without needing a browser

  @smoke
  Scenario: Service health check responds
    When I send a GET request to "/ping"
    Then the response status should be 201

  @smoke
  Scenario: Existing bookings can be listed
    When I send a GET request to "/booking"
    Then the response status should be 200
    And the response should contain a list of bookings

  @regression
  Scenario: A new booking can be created
    When I create a booking for "Jane" "Doe" with total price 150
    Then the response status should be 200
    And the response should contain a booking id
    And the response field "booking.firstname" should equal "Jane"
    And the response field "booking.totalprice" should equal "150"

  @regression
  Scenario: A created booking can be read back
    Given a new booking exists for "Jane" "Doe" with total price 150
    When I request the booking I created
    Then the response status should be 200
    And the response field "firstname" should equal "Jane"
    And the response field "lastname" should equal "Doe"
    And the response field "totalprice" should equal "150"

  @regression
  Scenario: A booking can be updated by an admin
    Given a new booking exists for "Jane" "Doe" with total price 150
    And I am logged in as an admin
    When I update the booking I created to total price 200
    Then the response status should be 200
    And the response field "totalprice" should equal "200"

  @regression
  Scenario: A booking can be deleted by an admin
    Given a new booking exists for "Jane" "Doe" with total price 150
    And I am logged in as an admin
    When I delete the booking I created
    Then the response status should be 201
    And the booking I created should no longer exist

  @negative
  Scenario: A missing booking returns not found
    When I request booking number 999999999
    Then the response status should be 404

  @negative
  Scenario: Login with wrong credentials is rejected
    When I log in with username "admin" and password "wrong-password"
    Then the response field "reason" should equal "Bad credentials"

  @negative
  Scenario: Deleting without logging in is forbidden
    Given a new booking exists for "Jane" "Doe" with total price 150
    When I try to delete the booking I created without logging in
    Then the response status should be 403
