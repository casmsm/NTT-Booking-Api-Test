@Booking
Feature: API Reservas de Hotel

  Como un usuario de la API de reservas de hotel
  Quiero gestionar las reservas a través de la API
  Para poder crear, consultar, actualizar y eliminar reservas

  @CP01
  Scenario: Crear token de autenticación exitosamente
    Given el actor establece el endpoint para crear un token de autenticacion
    When el actor envia una solicitud POST para autenticarse con usuario "admin" y password "password123"
    Then el codigo de respuesta deberia ser 200
    And el token deberia estar presente en la respuesta

  @CP02
  Scenario: Obtener todas las reservas exitosamente
    Given el actor establece el endpoint para obtener las reservas
    When el actor envia una solicitud GET para obtener todas las reservas
    Then el codigo de respuesta deberia ser 200

  @CP03
  Scenario: Crear una reserva exitosamente
    Given el actor establece el endpoint POST para crear una reserva
    When el actor envia una solicitud POST con los datos de la reserva "Carlos" "Santa María" "100" "false" "2025-05-31" "2025-06-05" "Breakfast"
    Then el codigo de respuesta deberia ser 200
    And la respuesta deberia contener el bookingid
    And los datos de la reserva deberian coincidir con los enviados

  @CP04
  Scenario Outline: Crear reservas con diferentes datos
    Given el actor establece el endpoint POST para crear una reserva
    When el actor envia una solicitud POST con los datos de la reserva "<firstname>" "<lastname>" "<totalprice>" "<depositpaid>" "<checkin>" "<checkout>" "<additionalneeds>"
    Then el codigo de respuesta deberia ser 200
    And la respuesta deberia contener el bookingid

    Examples:
      | firstname | lastname | totalprice | depositpaid | checkin    | checkout   | additionalneeds |
      | Juan      | Pérez    | 150        | true        | 2025-06-01 | 2025-06-07 | Late Checkout   |
      | María     | García   | 200        | false       | 2025-07-15 | 2025-07-20 | Extra Towels    |
      | Pedro     | López    | 300        | true        | 2025-08-10 | 2025-08-15 | Room Service    |

  @CP05
  Scenario: Obtener una reserva por ID exitosamente
    Given el actor establece el endpoint para obtener una reserva por ID
    And existe una reserva creada previamente
    When el actor envia una solicitud GET para obtener la reserva por ID
    Then el codigo de respuesta deberia ser 200
    And la respuesta deberia contener los datos de la reserva