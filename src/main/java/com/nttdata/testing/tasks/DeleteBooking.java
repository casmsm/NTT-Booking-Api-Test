package com.nttdata.testing.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import io.restassured.http.ContentType;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DeleteBooking implements Task {

    private Integer bookingId;

    public DeleteBooking(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public static DeleteBooking withId(Integer bookingId) {
        return instrumented(DeleteBooking.class, bookingId);
    }

    public static DeleteBooking byId() {
        return instrumented(DeleteBooking.class, (Integer) null);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Integer targetBookingId = bookingId;
        String token = actor.recall("authToken");

        // Si no se proporcionó un ID específico, usar el último creado
        if (targetBookingId == null) {
            targetBookingId = actor.recall("lastBookingId");
        }

        if (targetBookingId == null) {
            throw new RuntimeException("No se encontró un booking ID para eliminar");
        }

        actor.attemptsTo(
                Delete.from("/booking/" + targetBookingId)
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .header("Cookie", "token=" + token)
                        )
        );
    }
}