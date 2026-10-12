@ui
Feature: Infor website basic health checks
  As a QA engineer
  I want to confirm the key parts of the Infor website work
  So that problems are caught before customers see them

  @smoke
  Scenario: Home page loads correctly
    Given I open the Infor home page
    Then the page title should contain "Infor"
    And the page address should contain "infor.com"
    And the main heading should be visible

  @smoke
  Scenario: Header and footer are visible
    Given I open the Infor home page
    Then the page header should be visible
    And the page footer should be visible

  @regression
  Scenario Outline: Key pages open without errors
    When I open the "<path>" page of the Infor website
    Then the page should load without an error

    Examples:
      | path              |
      | /industries       |
      | /products         |
      | /platform         |
      | /customer-success |
      | /partners         |
      | /about            |
