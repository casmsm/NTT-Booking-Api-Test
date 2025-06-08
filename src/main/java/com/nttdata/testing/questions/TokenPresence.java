package com.nttdata.testing.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

public class TokenPresence implements Question<Boolean> {

    public static Question<Boolean> inResponse() {
        return new TokenPresence();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        try {
            String token = LastResponse.received().answeredBy(actor).path("token");
            return token != null && !token.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}