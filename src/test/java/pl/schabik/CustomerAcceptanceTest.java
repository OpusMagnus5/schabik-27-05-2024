package pl.schabik;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.schabik.application.command.createcustomer.CreateCustomerCommand;
import pl.schabik.application.command.createcustomer.CreateCustomerHandler;
import pl.schabik.domain.CustomerId;
import pl.schabik.infrastructure.ErrorResponse;
import pl.schabik.infrastructure.dto.GetCustomerResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CreateCustomerHandler createCustomerHandler;

    @Test
    @DisplayName("""
            given existing Customer id,
            when request is sent,
            then return Customer details and HTTP 200 status""")
    void givenExistingCustomerId_whenRequestIsSent_thenCustomerDetailsReturnedAndHttp200() {
        //given
        var customerId = CustomerId.newOne();
        var createCustomerDto = new CreateCustomerCommand(customerId, "Ferdzio", "Kiepski", "ferdek@gemail.com");
        createCustomerHandler.handle(createCustomerDto);

        //when
        var response = restTemplate.getForEntity(
                getBaseCustomersUrl() + "/" + customerId.id(), GetCustomerResponse.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", customerId.id())
                .hasFieldOrPropertyWithValue("firstName", createCustomerDto.firstName())
                .hasFieldOrPropertyWithValue("lastName", createCustomerDto.lastName())
                .hasFieldOrPropertyWithValue("email", createCustomerDto.email());
    }

    @Test
    @DisplayName("""
            given non-existing Customer id,
            when request is sent,
            then do not return Customer details and HTTP 404 status""")
    void givenNonExistingCustomerId_whenRequestIsSent_thenCustomerDetailsNotReturnedAndHttp404() {
        //given
        var notExistingId = UUID.randomUUID();

        //when
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(getBaseCustomersUrl() + "/" + notExistingId, ErrorResponse.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody())
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .extracting("message")
                .asString()
                .contains("Could not find customer");
    }

    @Test
    @DisplayName("""
            given request for creating Customer with all mandatory data correctly,
            when request is sent,
            then Customer is added and HTTP 200 status received""")
    void givenRequestForCreatingCustomer_whenRequestIsSent_thenCustomerAddedAndHttp200() {
        //given
        var createCustomerDto = new CreateCustomerCommand(CustomerId.newOne(), "Marianek", "Paździoch", "mario@gemail.com");

        //when
        var postResponse = restTemplate.postForEntity(getBaseCustomersUrl(), createCustomerDto, Void.class);

        //then
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getHeaders().getLocation()).isNotNull();

        var getResponse = restTemplate.getForEntity(postResponse.getHeaders().getLocation(), GetCustomerResponse.class).getBody();

        assertThat(getResponse)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("firstName", createCustomerDto.firstName())
                .hasFieldOrPropertyWithValue("lastName", createCustomerDto.lastName())
                .hasFieldOrPropertyWithValue("email", createCustomerDto.email());
    }

    @Test
    @DisplayName("""
            given request for creating Customer with existing email,
            when request is sent,
            then Customer not added and HTTP 409 status received""")
    void givenRequestForCreatingCustomerWithExistingEmail_whenRequestIsSent_thenCustomerNotAddedAndHttp409() {
        //given
        var email = "waldek12@gmail.com";
        createCustomerHandler.handle(new CreateCustomerCommand(CustomerId.newOne(), "Waldemar", "Kiepski", email));

        var createCustomerCommand = new CreateCustomerCommand(CustomerId.newOne(), "Walduś", "Boczek", email);

        //when
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(getBaseCustomersUrl(), createCustomerCommand, ErrorResponse.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getHeaders().getLocation()).isNull();
        assertThat(response.getBody())
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .extracting("message")
                .isNotNull();
    }

    private String getBaseCustomersUrl() {
        return "http://localhost:" + port + "/customers";
    }
}