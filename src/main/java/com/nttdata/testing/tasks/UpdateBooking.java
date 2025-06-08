package com.nttdata.testing.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class UpdateBooking implements Task {

    private String firstname;
    private String lastname;
    private String totalprice;
    private String depositpaid;
    private String checkin;
    private String checkout;
    private String additionalneeds;

    public UpdateBooking(String firstname, String lastname, String totalprice, String depositpaid,
                         String checkin, String checkout, String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.checkin = checkin;
        this.checkout = checkout;
        this.additionalneeds = additionalneeds;
    }

    public static UpdateBooking withData(String firstname, String lastname, String totalprice, String depositpaid,
                                         String checkin, String checkout, String additionalneeds) {
        return instrumented(UpdateBooking.class, firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Integer bookingId = actor.recall("lastBookingId");
        String token = actor.recall("authToken");

        if (bookingId == null) {
            throw new RuntimeException("No se encontró un booking ID para actualizar");
        }

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("firstname", firstname);
        bookingData.put("lastname", lastname);
        bookingData.put("totalprice", Integer.parseInt(totalprice));
        bookingData.put("depositpaid", Boolean.parseBoolean(depositpaid));
        bookingData.put("additionalneeds", additionalneeds);

        Map<String, String> bookingDates = new HashMap<>();
        bookingDates.put("checkin", checkin);
        bookingDates.put("checkout", checkout);
        bookingData.put("bookingdates", bookingDates);

        // Guardar datos enviados para validación posterior
        actor.remember("lastUpdateData", bookingData);

        actor.attemptsTo(
                Put.to("/booking/" + bookingId)
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .header("Accept", "application/json")
                                .header("Cookie", "token=" + token)
                                .body(bookingData)
                        )
        );
    }
}