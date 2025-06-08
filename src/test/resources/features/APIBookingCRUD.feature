@Booking @CRUDOperations
Feature: Operaciones CRUD de Reservas con Autenticación

  Como un usuario autenticado de la API
  Quiero realizar operaciones CRUD en reservas existentes
  Para gestionar el ciclo completo de una reserva

  Background:
    Given el actor establece el endpoint para gestionar reservas
    And el actor tiene un token de autenticacion valido
#    And existe una reserva creada previamente
    And existe la reserva creada:
      | firstname | lastname   | totalprice | depositpaid | checkin     | checkout    | additionalneeds |
      | Carlos    | Actualizado| 222        | false       | 2024-06-01  | 2024-06-10  | Late Checkout   |


  @CRUD @CP06
  Scenario: Actualizar una reserva completamente
    When el actor envia una solicitud PUT para actualizar la reserva con datos "Carlos" "Actualizado" "222" "false" "2024-06-01" "2024-06-10" "Late Checkout"
    Then el codigo de respuesta deberia ser 200
    And los datos actualizados deberian coincidir con los enviados

  @CRUD @CP07
  Scenario Outline: Actualizar una reserva completamente con distintos datos
    When el actor envia una solicitud PUT para actualizar la reserva con datos "<firstname>" "<lastname>" "<totalprice>" "<depositpaid>" "<checkin>" "<checkout>" "<additionalneeds>"
    Then el codigo de respuesta deberia ser 200
    And los datos actualizados deberian coincidir con los enviados

    Examples:
      | firstname | lastname   | totalprice | depositpaid | checkin     | checkout    | additionalneeds |
      | Carlos    | Actualizado| 222        | false       | 2025-06-01  | 2025-06-10  | Late Checkout   |
      | Maria     | Lopez      | 150        | true        | 2025-07-01  | 2025-07-05  | Breakfast       |
      | John      | Smith      | 300        | false       | 2025-08-10  | 2025-08-15  | Dinner          |

  @CRUD @CP08
  Scenario: Actualizar parcialmente una reserva
    When el actor envia una solicitud PATCH para actualizar parcialmente con firstname "Patch" y lastname "Patch Last Name"
    Then el codigo de respuesta deberia ser 200
    And los campos actualizados deberian coincidir con los enviados

  @CRUD @CP09
  Scenario: Eliminar una reserva exitosamente
    When el actor envia una solicitud DELETE para eliminar la reserva
    Then el codigo de respuesta deberia ser 201

  @CRUD @CP10
  Scenario: Actualizar solo el precio de la reserva
    When el actor actualiza solo el precio a 175
    Then el codigo de respuesta deberia ser 200
    And el campo precio deberia estar actualizado correctamente

  @CRUD @CP11
  Scenario: Actualizar solo las fechas de la reserva
    When el actor actualiza solo las fechas de checkin "2025-09-01" y checkout "2025-09-05"
    Then el codigo de respuesta deberia ser 200
    And las fechas deberian estar actualizadas correctamente

  @CRUD @CP12
  Scenario: Actualizar múltiples campos específicos
    When el actor actualiza los siguientes campos:
      | campo           | valor      |
      | firstname       | Roberto    |
      | totalprice      | 280        |
      | depositpaid     | true       |
      | additionalneeds | WiFi       |
    Then el codigo de respuesta deberia ser 200
    And los campos especificados deberian estar actualizados correctamente

  @CRUD @CP13
  Scenario: Actualizar fechas y necesidades adicionales
    When el actor actualiza los siguientes campos:
      | campo           | valor           |
      | checkin         | 2025-10-15      |
      | checkout        | 2025-10-20      |
      | additionalneeds | Spa Treatment   |
    Then el codigo de respuesta deberia ser 200
    And los campos especificados deberian estar actualizados correctamente

  @CRUD @CP14
  Scenario: Actualizar todos los campos usando HashMap
    When el actor actualiza los siguientes campos:
      | campo           | valor        |
      | firstname       | Alexandra    |
      | lastname        | Rodriguez    |
      | totalprice      | 450          |
      | depositpaid     | false        |
      | checkin         | 2025-11-01   |
      | checkout        | 2025-11-07   |
      | additionalneeds | Room Service |
    Then el codigo de respuesta deberia ser 200
    And todos los campos deberian estar actualizados correctamente

  @CRUD @CP15
  Scenario Outline: Actualizar campos específicos con diferentes valores
    When el actor actualiza los siguientes campos:
      | campo           | valor            |
      | firstname       | <firstname>      |
      | lastname        | <lastname>       |
      | totalprice      | <totalprice>     |
    Then el codigo de respuesta deberia ser 200
    And los campos especificados deberian estar actualizados correctamente

    Examples:
      | firstname | lastname  | totalprice |
      | Pedro     | Morales   | 180        |
      | Ana       | Gutierrez | 220        |
      | Luis      | Hernandez | 195        |