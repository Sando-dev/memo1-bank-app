Feature: Soporte a transacciones del tipo Extracción y Depósito
  Añadir la capacidad de realizar transacciones de extracción y depósito en una cuenta bancaria

  Scenario: Successfully create a withdrawal transaction
    Given Account with a balance of 1000
    When Creating a withdrawal transaction of 500
    Then Account balance should be 500

  Scenario: Successfully create a deposit transaction
    Given Account with a balance of 1000
    When Creating a deposit transaction of 500
    Then Account balance should be 1500
