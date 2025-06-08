package com.nttdata.testing.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import io.restassured.http.ContentType;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GetBookingById implements Task {

    private Integer bookingId;

    public GetBookingById(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public static GetBookingById withId(Integer bookingId) {
        return instrumented(GetBookingById.class, bookingId);
    }

    public static GetBookingById fromLastCreated() {
        return instrumented(GetBookingById.class, (Integer) null);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Integer targetBookingId = bookingId;

        // Si no se proporcionó un ID específico, usar el último creado
        if (targetBookingId == null) {
            targetBookingId = actor.recall("lastBookingId");
        }

        if (targetBookingId != null) {
            actor.attemptsTo(
                    Get.resource("/booking/" + targetBookingId)
                            .with(request -> request
                                    .header("Accept", "application/json")
                            )
            );
        } else {
            throw new RuntimeException("No se encontró un booking ID para consultar");
        }
    }
}