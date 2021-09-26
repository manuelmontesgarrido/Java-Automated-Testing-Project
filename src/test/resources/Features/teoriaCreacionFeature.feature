Feature: Ejemplo de creacion de una Feature
  As a customer,
  I want to create, edit and delete reports

  El proposito de la palabra Feature es proporcionar una descripcion de alto nivel
  de una funcion de software y agrupar escenarios relacionales

  Background:
    # En ocasiones nos encontramos con que repetimos los mismos pasos en todos los escenarios de la feature
    # En esta situación se pueden mover estos steps a un Background, y se ejecutará al inicio de cada escenario

    #Open the driver, insert the specified url and login to App
    Given I am in App main site
    # El paso Given o "Dado que..." se utiliza para describir el contexto inicial del sistema.
    # Su proposito es poner el sistema en un estado conocido desde que el usuario comience a interactuar
    # con el sistema.
    # Se pueden utilizar varios given seguidos pero es preferible no repetirlos y usar los steps And o But

    @test
      # La keyword para tag es @.
      # Se pueden asignar tags a features y escenarios para organizarlos y ejecutarlos según el criterio que queramos
      #
    Scenario: Handle Alerts
      # El escenario es un core de Gherkin
      # Un escenario ilustra una regla de negocio
      # Una feature puede tener uno o más escenarios
      # Consiste en una serie de pasos
      # La palabra Scenario es sinonimo de la palabra clave Example
      # Un escenario es un ejemplo de un comportamiento individual para establecer los criterios de aceptacion
      # Lo recomendable es que un escenario tenga entre 3 y 5 pasos

      # Steps:

      # When:
      # Se utiliza para describir un evento o una accion.
      # Puede ser una persona que interactua con el sistema o puede ser un evento desencadenado por otro sistema

      # Then:
      # Se utiliza para describir el resultado esperado.
      # La definicion del paso Then deberia usar una asercion para comparar el resultado real con el resultado esperado.
      Then Assert if App contains text App

      # And
      # Cuando tenemos varios Given, When o Then sucesivos, para no repetirlos usamos el And.

      And I click in JS element Example element
      And I click in element Example button
      And I wait 2 seconds
      Then I dismiss alert
      And I attach a Screenshot to Report: screenshot alert


