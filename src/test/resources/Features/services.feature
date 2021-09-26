Feature: Services
  As a customer,
  I want to create, edit and delete services

  Background:
    Open the driver and insert the specified url
    Login to pandora
    Load the DOM information
    Click in selected menu
    Assertion that we are in the right place

    Given I am in App main site
    Given I login in Pandora with user admin
    Then I load the DOM Information services_test.json
    And I click in JS element Services menu
    Then Assert if Services breadcrumb contains text Services

    @services
    Scenario: Create service
      And I click in element Create service button
      And I wait 1 seconds
      And I set element Service name input with text 000 Service for automated testing
      And I set element Service description input with text ## SERVICIO PARA PRUEBAS AUTOMÁTICAS ¡¡¡¡ NO BORRAR !!!##
      And I click in element Group service select
      Then I wait for element Group Applications option to be present
      And I click in element Group Applications option
      And I set element Agent service input with text 000
      Then I wait for element Agent service option to be present
      And I click in element Agent service option
      And I click in element Submit service button

    @services
    Scenario: Edit service
      And I click in element List of services icon
      And I click in element Edit service icon
      And I set element Service name input with text 000 A Service for automated testing EDIT!!
      And I set element Service description input with text Edit
      And I click in element Update service button
      Then Assert if Service name input is equal to 000 A Service for automated testing EDIT!!

    @services
    Scenario: Delete service
      And I click in element List of services icon
      And I click in element Delete service icon
      Then I accept alert
      Then Assert if Infobox services is equal to Service deleted successfully



