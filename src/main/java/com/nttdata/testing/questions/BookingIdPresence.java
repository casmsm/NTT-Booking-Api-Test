package com.nttdata.testing.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

public class BookingIdPresence implements Question<Boolean> {

    public static Question<Boolean> inResponse() {
        return new BookingIdPresence();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        try {
            Integer bookingId = LastResponse.received().answeredBy(actor).path("bookingid");
            if (bookingId != null) {
                // Guardamos el bookingId para uso posterior
                actor.remember("lastBookingId", bookingId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}