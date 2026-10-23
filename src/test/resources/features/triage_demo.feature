@triage-demo
Feature: Failure triage demo (these scenarios are meant to fail)
  Each scenario fails on purpose so you can see the triage report sort the failures into categories.
  Run them with: mvn clean test -Dcucumber.filter.tags="@triage-demo"

  Scenario: Demo - a record that does not exist (expected: test data)
    When I request booking number 999999999
    Then the response status should be 200

  Scenario: Demo - a wrong success code expected (expected: product defect)
    When I send a GET request to "/ping"
    Then the response status should be 200

  Scenario: Demo - a wrong value expected (expected: product defect)
    Given a new booking exists for "Jane" "Doe" with total price 150
    When I request the booking I created
    Then the response field "firstname" should equal "John"

  Scenario: Demo - an address that cannot be reached (expected: environment)
    When I send a GET request to an address that does not exist
    Then the response status should be 200
