package com.nttdata.testing.questions;

import io.restassured.path.json.JsonPath;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import java.util.Map;

public class BookingDataValidation implements Question<Boolean> {

    private String validationType;

    private BookingDataValidation(String validationType) {
        this.validationType = validationType;
    }

    public static Question<Boolean> areCorrect() {
        return new BookingDataValidation("create");
    }

    public static Question<Boolean> hasBookingData() {
        return new BookingDataValidation("get");
    }

    public static Question<Boolean> updatedDataMatches() {
        return new BookingDataValidation("update");
    }

    public static Question<Boolean> patchedDataMatches() {
        return new BookingDataValidation("patch");
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        try {
            switch (validationType) {
                case "create":
                    return validateCreateResponse(actor);
                case "get":
                    return validateGetResponse(actor);
                case "update":
                    return validateUpdateResponse(actor);
                case "patch":
                    return validatePatchResponse(actor);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean validateCreateResponse(Actor actor) {
        Map<String, Object> booking = LastResponse.received().answeredBy(actor).path("booking");
        Map<String, Object> sentData = actor.recall("lastBookingData");

        if (booking == null || sentData == null) return false;

        return booking.get("firstname").equals(sentData.get("firstname")) &&
                booking.get("lastname").equals(sentData.get("lastname")) &&
                booking.get("totalprice").equals(sentData.get("totalprice")) &&
                booking.get("depositpaid").equals(sentData.get("depositpaid")) &&
                booking.get("additionalneeds").equals(sentData.get("additionalneeds"));
    }

    private Boolean validateGetResponse(Actor actor) {
        Map<String, Object> booking = LastResponse.received().answeredBy(actor).getBody().as(Map.class);

        return booking != null &&
                booking.containsKey("firstname") &&
                booking.containsKey("lastname") &&
                booking.containsKey("totalprice") &&
                booking.containsKey("depositpaid") &&
                booking.containsKey("bookingdates") &&
                booking.containsKey("additionalneeds");
    }

    private Boolean validateUpdateResponse(Actor actor) {
        Map<String, Object> booking = LastResponse.received().answeredBy(actor).getBody().as(Map.class);
        Map<String, Object> sentData = actor.recall("lastUpdateData");

        if (booking == null || sentData == null) return false;

        return booking.get("firstname").equals(sentData.get("firstname")) &&
                booking.get("lastname").equals(sentData.get("lastname")) &&
                booking.get("totalprice").equals(sentData.get("totalprice")) &&
                booking.get("depositpaid").equals(sentData.get("depositpaid")) &&
                booking.get("additionalneeds").equals(sentData.get("additionalneeds"));
    }

    private Boolean validatePatchResponse(Actor actor) {
        Map<String, Object> booking = LastResponse.received().answeredBy(actor).getBody().as(Map.class);
        Map<String, Object> sentData = actor.recall("lastPatchData");

        if (booking == null || sentData == null) return false;

        // Validar solo los campos que se enviaron en el PATCH
        for (String key : sentData.keySet()) {
            if (!booking.get(key).equals(sentData.get(key))) {
                return false;
            }
        }
        return true;
    }

    public static Question<Boolean> specificFieldUpdated(String fieldName) {
        return actor -> {
            Map<String, Object> lastPatchData = actor.recall("lastPatchData");
            if (lastPatchData == null) {
                return false;
            }

            Object sentValue = lastPatchData.get(fieldName);
            if (sentValue == null) {
                return false;
            }

            JsonPath response = LastResponse.received().answeredBy(actor).jsonPath();
            Object responseValue = response.get(fieldName);

            // Comparación especial para números
            if (sentValue instanceof Integer && responseValue instanceof Integer) {
                return sentValue.equals(responseValue);
            }

            return sentValue.toString().equals(responseValue.toString());
        };
    }

    public static Question<Boolean> datesUpdated() {
        return actor -> {
            Map<String, Object> lastPatchData = actor.recall("lastPatchData");
            if (lastPatchData == null) {
                return false;
            }

            JsonPath response = LastResponse.received().answeredBy(actor).jsonPath();

            // Validar checkin si fue enviado
            if (lastPatchData.containsKey("checkin")) {
                String sentCheckin = (String) lastPatchData.get("checkin");
                String responseCheckin = response.getString("bookingdates.checkin");
                if (!sentCheckin.equals(responseCheckin)) {
                    return false;
                }
            }

            // Validar checkout si fue enviado
            if (lastPatchData.containsKey("checkout")) {
                String sentCheckout = (String) lastPatchData.get("checkout");
                String responseCheckout = response.getString("bookingdates.checkout");
                if (!sentCheckout.equals(responseCheckout)) {
                    return false;
                }
            }

            return true;
        };
    }

    public static Question<Boolean> dynamicFieldsUpdated() {
        return actor -> {
            Map<String, Object> lastPatchData = actor.recall("lastPatchData");
            if (lastPatchData == null) {
                return false;
            }

            JsonPath response = LastResponse.received().answeredBy(actor).jsonPath();

            for (Map.Entry<String, Object> entry : lastPatchData.entrySet()) {
                String fieldName = entry.getKey();
                Object sentValue = entry.getValue();

                Object responseValue;

                // Manejo especial para fechas anidadas
                if ("checkin".equals(fieldName)) {
                    responseValue = response.getString("bookingdates.checkin");
                } else if ("checkout".equals(fieldName)) {
                    responseValue = response.getString("bookingdates.checkout");
                } else {
                    responseValue = response.get(fieldName);
                }

                // Validación de valores
                if (sentValue instanceof Integer && responseValue instanceof Integer) {
                    if (!sentValue.equals(responseValue)) {
                        return false;
                    }
                } else if (sentValue instanceof Boolean && responseValue instanceof Boolean) {
                    if (!sentValue.equals(responseValue)) {
                        return false;
                    }
                } else {
                    if (!sentValue.toString().equals(responseValue.toString())) {
                        return false;
                    }
                }
            }

            return true;
        };
    }

    public static Question<Boolean> allFieldsUpdated() {
        return actor -> {
            Map<String, Object> lastPatchData = actor.recall("lastPatchData");
            if (lastPatchData == null) {
                return false;
            }

            JsonPath response = LastResponse.received().answeredBy(actor).jsonPath();

            // Lista de todos los campos posibles
            String[] allFields = {"firstname", "lastname", "totalprice", "depositpaid", "additionalneeds"};
            String[] dateFields = {"checkin", "checkout"};

            // Validar campos simples
            for (String field : allFields) {
                if (lastPatchData.containsKey(field)) {
                    Object sentValue = lastPatchData.get(field);
                    Object responseValue = response.get(field);

                    if (sentValue instanceof Integer && responseValue instanceof Integer) {
                        if (!sentValue.equals(responseValue)) {
                            return false;
                        }
                    } else if (sentValue instanceof Boolean && responseValue instanceof Boolean) {
                        if (!sentValue.equals(responseValue)) {
                            return false;
                        }
                    } else {
                        if (!sentValue.toString().equals(responseValue.toString())) {
                            return false;
                        }
                    }
                }
            }

            for (String dateField : dateFields) {
                if (lastPatchData.containsKey(dateField)) {
                    String sentValue = (String) lastPatchData.get(dateField);
                    String responseValue = response.getString("bookingdates." + dateField);
                    if (!sentValue.equals(responseValue)) {
                        return false;
                    }
                }
            }

            return true;
        };
    }
}