package pl.schabik.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.schabik.application.command.createorder.CreateOrderAddressDto;
import pl.schabik.application.command.createorder.CreateOrderCommand;
import pl.schabik.application.command.createorder.CreateOrderHandler;
import pl.schabik.application.command.createorder.CreateOrderItemDto;
import pl.schabik.application.common.exception.OrderNotFoundException;
import pl.schabik.application.query.getorder.GetOrderQuery;
import pl.schabik.application.query.getorder.GetOrderQueryHandler;
import pl.schabik.domain.Customer;
import pl.schabik.domain.CustomerId;
import pl.schabik.domain.OrderId;
import pl.schabik.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class GetOrderQueryHandlerTest {

    InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
    GetOrderQueryHandler getOrderQueryHandler = new GetOrderQueryHandler(orderRepository);

    CreateOrderHandler createOrderHandler = new CreateOrderHandler(orderRepository, customerRepository);

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void shouldGetOrder() {
        // given
        var customerId = CustomerId.newOne();
        var orderId = OrderId.newOne();
        customerRepository.save(new Customer(customerId, "Arnold", "Boczek", "boczek@gmail.com"));
        var createOrderCommand = getCreateOrderCommand(orderId, customerId);
        createOrderHandler.handle(createOrderCommand);

        // when
        var orderDto = getOrderQueryHandler.handle(orderId);

        // then
        assertThat(orderDto)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("customerId", createOrderCommand.customerId().id())
                .hasFieldOrPropertyWithValue("price", createOrderCommand.price())
                .hasFieldOrPropertyWithValue("status", OrderStatus.PENDING)
                .extracting(GetOrderQuery::address)
                .hasFieldOrPropertyWithValue("street", createOrderCommand.address().street())
                .hasFieldOrPropertyWithValue("postalCode", createOrderCommand.address().postalCode())
                .hasFieldOrPropertyWithValue("city", createOrderCommand.address().city())
                .hasFieldOrPropertyWithValue("houseNo", createOrderCommand.address().houseNo());

        assertThat(orderDto.items()).hasSize(createOrderCommand.items().size())
                .zipSatisfy(createOrderCommand.items(), (dto, create) -> {
                    assertThat(dto.productId()).isEqualTo(create.productId());
                    assertThat(dto.price()).isEqualTo(create.price());
                    assertThat(dto.quantity()).isEqualTo(create.quantity());
                    assertThat(dto.totalPrice()).isEqualTo(create.totalPrice());
                });
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {
        // given
        var nonExistentOrderId = OrderId.newOne();

        // expected
        assertThatThrownBy(() -> getOrderQueryHandler.handle(nonExistentOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(OrderNotFoundException.createExceptionMessage(nonExistentOrderId));
    }

    private CreateOrderCommand getCreateOrderCommand(OrderId orderId, CustomerId customerId) {
        var items = List.of(new CreateOrderItemDto(UUID.randomUUID(), 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new CreateOrderItemDto(UUID.randomUUID(), 1, new BigDecimal("34.56"), new BigDecimal("34.56")));
        var address = new CreateOrderAddressDto("Małysza", "94-000", "Adasiowo", "12");
        return new CreateOrderCommand(orderId, customerId, new BigDecimal("54.56"), items, address);
    }
}