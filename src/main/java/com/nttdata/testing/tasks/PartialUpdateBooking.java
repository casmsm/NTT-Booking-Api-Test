package com.nttdata.testing.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class PartialUpdateBooking implements Task {

    private Map<String, Object> fieldsToUpdate;

    public PartialUpdateBooking(Map<String, Object> fieldsToUpdate) {
        this.fieldsToUpdate = fieldsToUpdate;
    }

    public static PartialUpdateBooking withNames(String firstname, String lastname) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("firstname", firstname);
        fields.put("lastname", lastname);
        return instrumented(PartialUpdateBooking.class, fields);
    }

    public static PartialUpdateBooking withFields(Map<String, Object> fields) {
        return instrumented(PartialUpdateBooking.class, fields);
    }

    public static PartialUpdateBooking withDynamicFields(Map<String, Object> inputFields) {
        Map<String, Object> body = buildBodyFromHashMap(inputFields);
        return instrumented(PartialUpdateBooking.class, body);
    }

    private static Map<String, Object> buildBodyFromHashMap(Map<String, Object> inputFields) {
        Map<String, Object> body = new HashMap<>();

        // Campos simples
        if (inputFields.containsKey("firstname")) {
            body.put("firstname", inputFields.get("firstname"));
        }
        if (inputFields.containsKey("lastname")) {
            body.put("lastname", inputFields.get("lastname"));
        }
        if (inputFields.containsKey("totalprice")) {
            body.put("totalprice", inputFields.get("totalprice"));
        }
        if (inputFields.containsKey("depositpaid")) {
            body.put("depositpaid", inputFields.get("depositpaid"));
        }
        if (inputFields.containsKey("additionalneeds")) {
            body.put("additionalneeds", inputFields.get("additionalneeds"));
        }

        Map<String, String> bookingDates = new HashMap<>();
        boolean hasBookingDates = false;

        if (inputFields.containsKey("checkin")) {
            bookingDates.put("checkin", (String) inputFields.get("checkin"));
            hasBookingDates = true;
        }
        if (inputFields.containsKey("checkout")) {
            bookingDates.put("checkout", (String) inputFields.get("checkout"));
            hasBookingDates = true;
        }

        if (hasBookingDates) {
            body.put("bookingdates", bookingDates);
        }

        return body;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Integer bookingId = actor.recall("lastBookingId");
        String token = actor.recall("authToken");

        if (bookingId == null) {
            throw new RuntimeException("No se encontró un booking ID para actualizar parcialmente");
        }

        // Guardar datos enviados para validación posterior
        actor.remember("lastPatchData", fieldsToUpdate);

        System.out.println("PATCH Body enviado: " + fieldsToUpdate);

        actor.attemptsTo(
                Patch.to("/booking/" + bookingId)
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .header("Accept", "application/json")
                                .header("Cookie", "token=" + token)
                                .body(fieldsToUpdate)
                        )
        );
    }
}