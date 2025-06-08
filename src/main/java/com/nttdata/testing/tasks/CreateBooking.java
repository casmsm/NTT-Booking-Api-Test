package com.nttdata.testing.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.rest.SerenityRest;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CreateBooking implements Task {

    private String firstname;
    private String lastname;
    private String totalprice;
    private String depositpaid;
    private String checkin;
    private String checkout;
    private String additionalneeds;

    public CreateBooking(String firstname, String lastname, String totalprice, String depositpaid,
                         String checkin, String checkout, String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.checkin = checkin;
        this.checkout = checkout;
        this.additionalneeds = additionalneeds;
    }

    public static CreateBooking withData(String firstname, String lastname, String totalprice, String depositpaid, String checkin, String checkout, String additionalneeds) {
        return instrumented(CreateBooking.class, firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds);
    }

    public static CreateBooking withDefaultData() {
        return instrumented(CreateBooking.class, "Carlos", "Santa María", "100", "false", "2025-05-31", "2025-06-05", "Breakfast");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
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
        actor.remember("lastBookingData", bookingData);

        actor.attemptsTo(
                Post.to("/booking")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .header("Accept", "application/json")
                                .body(bookingData)
                        )
        );

        // Guardar el bookingId si se creó exitosamente
        try {
            Integer bookingId = SerenityRest.lastResponse().path("bookingid");
            if (bookingId != null) {
                actor.remember("lastBookingId", bookingId);
            }
        } catch (Exception e) {
            // BookingId no disponible
        }
    }
}