
package pl.schabik.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.schabik.common.CustomerCreatedEvent;
import pl.schabik.order.application.OrderApplicationService;
import pl.schabik.order.application.dto.CreateOrderAddressDto;
import pl.schabik.order.application.dto.CreateOrderDto;
import pl.schabik.order.application.dto.CreateOrderItemDto;
import pl.schabik.order.application.dto.OrderDto;
import pl.schabik.order.application.replication.CustomerProjectionService;
import pl.schabik.order.domain.vo.OrderId;
import pl.schabik.order.domain.OrderStatus;

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
    private CustomerProjectionService customerService;

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Test
    @DisplayName("""
            given request to add order for existing customer,
            when request is sent,
            then save order and HTTP 200 status received""")
    void givenRequestToAddOrderForExistingCustomer_whenRequestIsSent_thenOrderSavedAndHttp200() {
        // given
        var createOrderDto = createOrderDto();

        // when
        ResponseEntity<UUID> response = restTemplate.postForEntity(getBaseUrl(), createOrderDto, UUID.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        var savedOrder = orderApplicationService.getOrderById(new OrderId(response.getBody()));
        assertThat(savedOrder)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("customerId", createOrderDto.customerId())
                .hasFieldOrPropertyWithValue("price", createOrderDto.price())
                .hasFieldOrPropertyWithValue("status", OrderStatus.PENDING)
                .extracting(OrderDto::address)
                .hasFieldOrPropertyWithValue("street", createOrderDto.address().street())
                .hasFieldOrPropertyWithValue("postalCode", createOrderDto.address().postalCode())
                .hasFieldOrPropertyWithValue("city", createOrderDto.address().city())
                .hasFieldOrPropertyWithValue("houseNo", createOrderDto.address().houseNo());

        assertThat(savedOrder.items()).hasSize(createOrderDto.items().size())
                .zipSatisfy(createOrderDto.items(), (orderItem, orderItemDto) -> {
                    assertThat(orderItem.productId()).isEqualTo(orderItemDto.productId());
                    assertThat(orderItem.price()).isEqualTo(orderItemDto.price());
                    assertThat(orderItem.quantity()).isEqualTo(orderItemDto.quantity());
                    assertThat(orderItem.totalPrice()).isEqualTo(orderItemDto.totalPrice());
                });
    }

    private CreateOrderDto createOrderDto() {
        var customerId = UUID.randomUUID();
        customerService.replicateCustomer(new CustomerCreatedEvent(customerId));

        var items = List.of(new CreateOrderItemDto(UUID.randomUUID(), 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new CreateOrderItemDto(UUID.randomUUID(), 1, new BigDecimal("34.56"), new BigDecimal("34.56")));
        var address = new CreateOrderAddressDto("Małysza", "94-000", "Adasiowo", "12");
        return new CreateOrderDto(customerId, new BigDecimal("54.56"), items, address);
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/orders";
    }
}