@ui @mobile
Feature: Infor website on mobile devices
  As a QA engineer
  I want to confirm the website works on phone and tablet screens
  So that mobile visitors get a usable page

  @regression
  Scenario Outline: Home page works on a mobile device
    Given I use a "<device>" device
    And I open the Infor home page
    Then the emulated screen width should be <width> pixels
    And the page title should contain "Infor"
    And the main heading should be visible
    And the page should declare a mobile viewport
    And the page should not scroll sideways

    Examples:
      | device        | width |
      | Pixel 7       | 412   |
      | iPhone 12 Pro | 390   |
      | iPad Air      | 820   |
