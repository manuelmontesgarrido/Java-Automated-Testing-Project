Feature: Basic google search
  As a customer,
  I want to do a basic google search

  @test
  Scenario: Basic google search
    Given I am in App main site
    Then I load the DOM Information frames.json
    And I click in element Accept cookies
    And I set element Google search input with text Como revisar un proyecto de github
    And I click in element Google search button
    And I wait 1 seconds
    And I click in element Link to Github documentation
    Then Assert if Github logo is equal to GitHub Docs


