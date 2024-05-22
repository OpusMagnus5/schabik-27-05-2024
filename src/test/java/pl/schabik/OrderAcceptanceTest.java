
package pl.schabik;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import pl.schabik.domain.*;
import pl.schabik.infrastructure.dto.CreateOrderRequest;
import pl.schabik.infrastructure.dto.GetOrderResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("""
            given request to add order for existing customer,
            when request is sent,
            then save order and HTTP 200 status received""")
    void givenRequestToAddOrderForExistingCustomer_whenRequestIsSent_thenOrderSavedAndHttp200() {
        // given
        var orderRequest = createOrderRequest();

        // when
        var postResponse = restTemplate.postForEntity(getBaseUrl(), orderRequest, Void.class);

        //then
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getHeaders().getLocation()).isNotNull();

        var getResponse = restTemplate.getForEntity(postResponse.getHeaders().getLocation(), GetOrderResponse.class).getBody();

        assertThat(getResponse)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("customerId", orderRequest.customerId())
                .hasFieldOrPropertyWithValue("price", orderRequest.price())
                .hasFieldOrPropertyWithValue("status", OrderStatus.PENDING)
                .extracting(GetOrderResponse::address)
                .hasFieldOrPropertyWithValue("street", orderRequest.address().street())
                .hasFieldOrPropertyWithValue("postalCode", orderRequest.address().postalCode())
                .hasFieldOrPropertyWithValue("city", orderRequest.address().city())
                .hasFieldOrPropertyWithValue("houseNo", orderRequest.address().houseNo());

        assertThat(getResponse.items()).isNotNull()
                .hasSize(orderRequest.items().size())
                .zipSatisfy(orderRequest.items(), (orderItem, orderItemDto) -> {
                    assertThat(orderItem.productId()).isEqualTo(orderItemDto.productId());
                    assertThat(orderItem.price()).isEqualTo(orderItemDto.price());
                    assertThat(orderItem.quantity()).isEqualTo(orderItemDto.quantity());
                    assertThat(orderItem.totalPrice()).isEqualTo(orderItemDto.totalPrice());
                });
    }

    private CreateOrderRequest createOrderRequest() {
        var customerId = CustomerId.newOne();
        customerRepository.save(new Customer(customerId, "Waldek", "Kiepski", "waldek@gmail.com"));

        var items = List.of(new CreateOrderRequest.OrderItemRequest(UUID.randomUUID(), 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new CreateOrderRequest.OrderItemRequest(UUID.randomUUID(), 1, new BigDecimal("34.56"), new BigDecimal("34.56")));
        var address = new CreateOrderRequest.OrderAddressRequest("Małysza", "94-000", "Adasiowo", "12");
        return new CreateOrderRequest(customerId.id(), new BigDecimal("54.56"), items, address);
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/orders";
    }
}
