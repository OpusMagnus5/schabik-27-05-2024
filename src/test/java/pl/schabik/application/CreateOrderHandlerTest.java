package pl.schabik.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.schabik.application.command.createorder.CreateOrderAddressDto;
import pl.schabik.application.command.createorder.CreateOrderCommand;
import pl.schabik.application.command.createorder.CreateOrderHandler;
import pl.schabik.application.command.createorder.CreateOrderItemDto;
import pl.schabik.application.common.exception.CustomerNotFoundException;
import pl.schabik.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CreateOrderHandlerTest {

    InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
    CreateOrderHandler orderService = new CreateOrderHandler(orderRepository, customerRepository);

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() {
        // given
        var orderId = OrderId.newOne();
        var customerId = CustomerId.newOne();
        customerRepository.save(new Customer(customerId, "Arnold", "Boczek", "boczek@gmail.com"));
        var createOrderCommand = getCreateOrderCommand(orderId, customerId);

        // when
        orderService.handle(createOrderCommand);

        // then
        var savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("customer.id", createOrderCommand.customerId())
                .hasFieldOrPropertyWithValue("price", new Money(createOrderCommand.price()))
                .hasFieldOrPropertyWithValue("status", OrderStatus.PENDING)
                .extracting(Order::getAddress)
                .hasFieldOrPropertyWithValue("street", createOrderCommand.address().street())
                .hasFieldOrPropertyWithValue("postalCode", createOrderCommand.address().postalCode())
                .hasFieldOrPropertyWithValue("city", createOrderCommand.address().city())
                .hasFieldOrPropertyWithValue("houseNo", createOrderCommand.address().houseNo());

        assertThat(savedOrder.getItems()).hasSize(createOrderCommand.items().size())
                .zipSatisfy(createOrderCommand.items(), (orderItem, orderItemDto) -> {
                    assertThat(orderItem.getProductId()).isEqualTo(orderItemDto.productId());
                    assertThat(orderItem.getPrice()).isEqualTo(new Money(orderItemDto.price()));
                    assertThat(orderItem.getQuantity()).isEqualTo(new Quantity(orderItemDto.quantity()));
                    assertThat(orderItem.getTotalPrice()).isEqualTo(new Money(orderItemDto.totalPrice()));
                });
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExistWhileCreatingOrder() {
        // given
        var nonExistentCustomerId = CustomerId.newOne();
        var createOrderCommand = getCreateOrderCommand(OrderId.newOne(), nonExistentCustomerId);

        // expected
        assertThatThrownBy(() -> orderService.handle(createOrderCommand))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage(CustomerNotFoundException.createExceptionMessage(nonExistentCustomerId));
    }

    private CreateOrderCommand getCreateOrderCommand(OrderId orderId, CustomerId customerId) {
        var items = List.of(new CreateOrderItemDto(UUID.randomUUID(), 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new CreateOrderItemDto(UUID.randomUUID(), 1, new BigDecimal("34.56"), new BigDecimal("34.56")));
        var address = new CreateOrderAddressDto("Małysza", "94-000", "Adasiowo", "12");
        return new CreateOrderCommand(orderId, customerId, new BigDecimal("54.56"), items, address);
    }
}