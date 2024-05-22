package pl.schabik.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.schabik.application.command.createorder.CreateOrderAddressDto;
import pl.schabik.application.command.createorder.CreateOrderCommand;
import pl.schabik.application.command.createorder.CreateOrderHandler;
import pl.schabik.application.command.createorder.CreateOrderItemDto;
import pl.schabik.application.command.payorder.PayOrderCommand;
import pl.schabik.application.command.payorder.PayOrderHandler;
import pl.schabik.application.common.exception.OrderNotFoundException;
import pl.schabik.domain.Customer;
import pl.schabik.domain.CustomerId;
import pl.schabik.domain.OrderId;
import pl.schabik.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PayOrderHandlerTest {

    InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
    CreateOrderHandler createOrderHandler = new CreateOrderHandler(orderRepository, customerRepository);
    PayOrderHandler payOrderHandler = new PayOrderHandler(orderRepository);

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void shouldPaidOrder() {
        // given
        var customerId = CustomerId.newOne();
        var orderId = OrderId.newOne();
        customerRepository.save(new Customer(customerId, "Arnold", "Boczek", "boczek@gmail.com"));
        var createOrderDto = getCreateOrderCommand(orderId, customerId);
        createOrderHandler.handle(createOrderDto);

        // when
        payOrderHandler.handle(new PayOrderCommand(orderId));

        // then
        var paidOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExistForPayment() {
        // given
        var nonExistentOrderId = OrderId.newOne();

        // expected
        assertThatThrownBy(() -> payOrderHandler.handle(new PayOrderCommand(nonExistentOrderId)))
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