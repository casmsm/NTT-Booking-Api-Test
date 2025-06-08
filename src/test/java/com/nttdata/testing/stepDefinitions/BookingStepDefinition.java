package com.nttdata.testing.stepDefinitions;

import com.nttdata.testing.questions.ResponseCode;
import com.nttdata.testing.questions.TokenPresence;
import com.nttdata.testing.questions.BookingIdPresence;
import com.nttdata.testing.questions.BookingDataValidation;
import com.nttdata.testing.tasks.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class BookingStepDefinition {

    public static Logger LOGGER = LoggerFactory.getLogger(BookingStepDefinition.class);

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    // Auth Steps
    @Given("el {actor} establece el endpoint para crear un token de autenticacion")
    public void elActorEstableceElEndpointParaCrearUnTokenDeAutenticacion(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    @When("el {actor} envia una solicitud POST para autenticarse con usuario {string} y password {string}")
    public void elActorEnviaUnaSolicitudPOSTParaAutenticarseConUsuarioYPassword(Actor actor, String username, String password) {
        actor.attemptsTo(CreateToken.withCredentials(username, password));
    }

    @Then("el token deberia estar presente en la respuesta")
    public void elTokenDeberiaEstarPresenteEnLaRespuesta() {
        theActorInTheSpotlight().should(seeThat("El token está presente", TokenPresence.inResponse(), is(true)));
    }

    // Get All Bookings Steps
    @Given("el {actor} establece el endpoint para obtener las reservas")
    public void elActorEstableceElEndpointParaObtenerLasReservas(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    @When("el {actor} envia una solicitud GET para obtener todas las reservas")
    public void elActorEnviaUnaSolicitudGETParaObtenerTodasLasReservas(Actor actor) {
        actor.attemptsTo(GetAllBookings.fromEndpoint());
    }

    // Create Booking Steps
    @Given("el {actor} establece el endpoint POST para crear una reserva")
    public void elActorEstableceElEndpointPOSTParaCrearUnaReserva(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    @When("el {actor} envia una solicitud POST con los datos de la reserva {string} {string} {string} {string} {string} {string} {string}")
    public void elActorEnviaUnaSolicitudPOSTConLosDatosDeLaReserva(Actor actor, String firstname, String lastname,
                                                                   String totalprice, String depositpaid, String checkin, String checkout, String additionalneeds) {
        actor.attemptsTo(CreateBooking.withData(firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds));
    }

    @Then("la respuesta deberia contener el bookingid")
    public void laRespuestaDeberiaContenerElBookingid() {
        theActorInTheSpotlight().should(seeThat("El bookingId está presente", BookingIdPresence.inResponse(), is(true)));
    }

    @And("los datos de la reserva deberian coincidir con los enviados")
    public void losDatosDeLaReservaDeberianCoincidirConLosEnviados() {
        theActorInTheSpotlight().should(seeThat("Los datos de la reserva coinciden", BookingDataValidation.areCorrect(), is(true)));
    }

    // Get Booking by ID Steps
    @Given("el {actor} establece el endpoint para obtener una reserva por ID")
    public void elActorEstableceElEndpointParaObtenerUnaReservaPorID(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    @And("existe una reserva creada previamente")
    public void existeUnaReservaCreadaPreviamente() {
        theActorInTheSpotlight().attemptsTo(CreateBooking.withDefaultData());
    }

    @When("el {actor} envia una solicitud GET para obtener la reserva por ID")
    public void elActorEnviaUnaSolicitudGETParaObtenerLaReservaPorID(Actor actor) {
        actor.attemptsTo(GetBookingById.fromLastCreated());
    }

    @And("la respuesta deberia contener los datos de la reserva")
    public void laRespuestaDeberiaContenerLosDatosDeLaReserva() {
        theActorInTheSpotlight().should(seeThat("La respuesta contiene datos de reserva", BookingDataValidation.hasBookingData(), is(true)));
    }

    // Update Booking Steps
    @Given("el {actor} establece el endpoint para actualizar una reserva")
    public void elActorEstableceElEndpointParaActualizarUnaReserva(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    @And("el {actor} tiene un token de autenticacion valido")
    public void elActorTieneUnTokenDeAutenticacionValido(Actor actor) {
        actor.attemptsTo(CreateToken.withDefaultCredentials());
    }

    @When("el {actor} envia una solicitud PUT para actualizar la reserva con datos {string} {string} {string} {string} {string} {string} {string}")
    public void elActorEnviaUnaSolicitudPUTParaActualizarLaReservaConDatos(Actor actor, String firstname, String lastname, String totalprice, String depositpaid, String checkin, String checkout, String additionalneeds) {
        actor.attemptsTo(UpdateBooking.withData(firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds));
    }

    @And("los datos actualizados deberian coincidir con los enviados")
    public void losDatosActualizadosDeberianCoincidirConLosEnviados() {
        theActorInTheSpotlight().should(seeThat("Los datos actualizados coinciden", BookingDataValidation.updatedDataMatches(), is(true)));
    }

    // Partial Update Steps - Existing
    @Given("el {actor} establece el endpoint para actualizar parcialmente una reserva")
    public void elActorEstableceElEndpointParaActualizarParcialmenteUnaReserva(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    @When("el {actor} envia una solicitud PATCH para actualizar parcialmente con firstname {string} y lastname {string}")
    public void elActorEnviaUnaSolicitudPATCHParaActualizarParcialmenteConFirstnameYLastname(Actor actor, String firstname, String lastname) {
        actor.attemptsTo(PartialUpdateBooking.withNames(firstname, lastname));
    }

    @And("los campos actualizados deberian coincidir con los enviados")
    public void losCamposActualizadosDeberianCoincidirConLosEnviados() {
        theActorInTheSpotlight().should(seeThat("Los campos actualizados coinciden", BookingDataValidation.patchedDataMatches(), is(true)));
    }

    // Actualizar solo precio
    @When("el {actor} actualiza solo el precio a {int}")
    public void elActorActualizaSoloElPrecio(Actor actor, Integer precio) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("totalprice", precio);

        actor.attemptsTo(PartialUpdateBooking.withDynamicFields(fields));
    }

    @And("el campo precio deberia estar actualizado correctamente")
    public void elCampoPrecioDeberiaEstarActualizadoCorrectamente() {
        theActorInTheSpotlight().should(seeThat("El precio está actualizado", BookingDataValidation.specificFieldUpdated("totalprice"), is(true)));
    }

    // Actualizar solo fechas
    @When("el {actor} actualiza solo las fechas de checkin {string} y checkout {string}")
    public void elActorActualizaSoloLasFechas(Actor actor, String checkin, String checkout) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("checkin", checkin);
        fields.put("checkout", checkout);

        actor.attemptsTo(PartialUpdateBooking.withDynamicFields(fields));
    }

    @And("las fechas deberian estar actualizadas correctamente")
    public void lasFechasDeberianEstarActualizadasCorrectamente() {
        theActorInTheSpotlight().should(seeThat("Las fechas están actualizadas", BookingDataValidation.datesUpdated(), is(true)));
    }

    // Actualizar múltiples campos usando DataTable
    @When("el {actor} actualiza los siguientes campos:")
    public void elActorActualizaLosSiguientesCampos(Actor actor, DataTable dataTable) {
        Map<String, Object> fields = new HashMap<>();

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String campo = row.get("campo");
            String valor = row.get("valor");

            // Conversión de tipos según el campo
            switch (campo) {
                case "totalprice":
                    fields.put(campo, Integer.parseInt(valor));
                    break;
                case "depositpaid":
                    fields.put(campo, Boolean.parseBoolean(valor));
                    break;
                default:
                    fields.put(campo, valor);
                    break;
            }
        }

        LOGGER.info("Actualizando campos: {}", fields);
        actor.attemptsTo(PartialUpdateBooking.withDynamicFields(fields));
    }

    @And("los campos especificados deberian estar actualizados correctamente")
    public void losCamposEspecificadosDeberianEstarActualizadosCorrectamente() {
        theActorInTheSpotlight().should(seeThat("Los campos especificados están actualizados", BookingDataValidation.dynamicFieldsUpdated(), is(true)));
    }

    @And("todos los campos deberian estar actualizados correctamente")
    public void todosLosCamposDeberianEstarActualizadosCorrectamente() {
        theActorInTheSpotlight().should(seeThat("Todos los campos están actualizados", BookingDataValidation.allFieldsUpdated(), is(true)));
    }

    // Delete Booking Steps
    @Given("el {actor} establece el endpoint para eliminar una reserva")
    public void elActorEstableceElEndpointParaEliminarUnaReserva(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    @When("el {actor} envia una solicitud DELETE para eliminar la reserva")
    public void elActorEnviaUnaSolicitudDELETEParaEliminarLaReserva(Actor actor) {
        actor.attemptsTo(DeleteBooking.byId());
    }

    // CRUD
    @Given("el {actor} establece el endpoint para gestionar reservas")
    public void el_actor_establece_el_endpoint_para_gestionar_reservas(Actor actor) {
        actor.whoCan(CallAnApi.at("https://restful-booker.herokuapp.com"));
    }

    // Common Step
    @Then("el codigo de respuesta deberia ser {int}")
    public void elCodigoDeRespuestaDeberiaSer(int responseCode) {
        theActorInTheSpotlight().should(seeThat("El código de respuesta", ResponseCode.getStatus(), equalTo(responseCode)));
    }

    @And("existe la reserva creada:")
    public void existeLaReservaCreada(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = data.get(0);

        String firstname = row.get("firstname");
        String lastname = row.get("lastname");
        String totalprice = row.get("totalprice");
        String depositpaid = row.get("depositpaid");
        String checkin = row.get("checkin");
        String checkout = row.get("checkout");
        String additionalneeds = row.get("additionalneeds");

        theActorInTheSpotlight().attemptsTo(CreateBooking.withData(firstname, lastname, totalprice, depositpaid, checkin, checkout, additionalneeds));
    }
}