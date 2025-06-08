package com.nttdata.testing.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.rest.SerenityRest;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CreateToken implements Task {

    private String username;
    private String password;

    public CreateToken(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static CreateToken withCredentials(String username, String password) {
        return instrumented(CreateToken.class, username, password);
    }

    public static CreateToken withDefaultCredentials() {
        return instrumented(CreateToken.class, "admin", "password123");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        actor.attemptsTo(
                Post.to("/auth")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .body(credentials)
                        )
        );

        // Guardar el token para uso posterior
        try {
            String token = SerenityRest.lastResponse().path("token");
            if (token != null) {
                actor.remember("authToken", token);
            }
        } catch (Exception e) {
            // Token no disponible
        }
    }
}