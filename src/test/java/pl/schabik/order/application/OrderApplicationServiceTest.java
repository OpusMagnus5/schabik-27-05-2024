package pl.schabik.order.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.schabik.common.CustomerCreatedEvent;
import pl.schabik.order.application.dto.CreateOrderAddressDto;
import pl.schabik.order.application.dto.CreateOrderDto;
import pl.schabik.order.application.dto.CreateOrderItemDto;
import pl.schabik.order.application.dto.OrderDto;
import pl.schabik.order.application.exception.CustomerNotFoundException;
import pl.schabik.order.application.exception.OrderNotFoundException;
import pl.schabik.order.application.replication.CustomerProjectionService;
import pl.schabik.order.application.replication.InMemoryCustomerProjectionRepository;
import pl.schabik.order.domain.Order;
import pl.schabik.order.domain.OrderDomainService;
import pl.schabik.order.domain.OrderStatus;
import pl.schabik.order.domain.vo.Money;
import pl.schabik.order.domain.vo.OrderId;
import pl.schabik.order.domain.vo.Quantity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OrderApplicationServiceTest {

    InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    InMemoryCustomerProjectionRepository customerRepository = new InMemoryCustomerProjectionRepository();
    CustomerProjectionService customerService = new CustomerProjectionService(customerRepository);
    OrderDomainService orderDomainService = new OrderDomainService();
    OrderApplicationService orderApplicationService = new OrderApplicationService(orderRepository, customerService, orderDomainService);

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() {
        // given
        var customerId = UUID.randomUUID();
        customerService.replicateCustomer(new CustomerCreatedEvent(customerId));
        var createOrderDto = getCreateOrderDto(customerId);

        // when
        var orderId = orderApplicationService.createOrder(createOrderDto);

        // then
        var savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("customerId", createOrderDto.customerId())
                .hasFieldOrPropertyWithValue("price", new Money(createOrderDto.price()))
                .hasFieldOrPropertyWithValue("status", OrderStatus.PENDING)
                .extracting(Order::getAddress)
                .hasFieldOrPropertyWithValue("street", createOrderDto.address().street())
                .hasFieldOrPropertyWithValue("postalCode", createOrderDto.address().postalCode())
                .hasFieldOrPropertyWithValue("city", createOrderDto.address().city())
                .hasFieldOrPropertyWithValue("houseNo", createOrderDto.address().houseNo());

        assertThat(savedOrder.getItems()).hasSize(createOrderDto.items().size())
                .zipSatisfy(createOrderDto.items(), (orderItem, orderItemDto) -> {
                    assertThat(orderItem.getProductId()).isEqualTo(orderItemDto.productId());
                    assertThat(orderItem.getPrice()).isEqualTo(new Money(orderItemDto.price()));
                    assertThat(orderItem.getQuantity()).isEqualTo(new Quantity(orderItemDto.quantity()));
                    assertThat(orderItem.getTotalPrice()).isEqualTo(new Money(orderItemDto.totalPrice()));
                });
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExistWhileCreatingOrder() {
        // given
        var nonExistentCustomerId = UUID.randomUUID();
        var createOrderDto = getCreateOrderDto(nonExistentCustomerId);

        // expected
        assertThatThrownBy(() -> orderApplicationService.createOrder(createOrderDto))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage(CustomerNotFoundException.createExceptionMessage(nonExistentCustomerId));
    }

    @Test
    void shouldPaidOrder() {
        // given
        var customerId = UUID.randomUUID();
        customerService.replicateCustomer(new CustomerCreatedEvent(customerId));
        var createOrderDto = getCreateOrderDto(customerId);
        var orderId = orderApplicationService.createOrder(createOrderDto);

        // when
        orderApplicationService.pay(orderId);

        // then
        var paidOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExistForPayment() {
        // given
        var nonExistentOrderId = new OrderId(UUID.randomUUID());

        // expected
        assertThatThrownBy(() -> orderApplicationService.pay(nonExistentOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(OrderNotFoundException.createExceptionMessage(nonExistentOrderId));
    }

    @Test
    void shouldGetOrder() {
        // given
        var customerId = UUID.randomUUID();
        customerService.replicateCustomer(new CustomerCreatedEvent(customerId));
        var createOrderDto = getCreateOrderDto(customerId);
        var orderId = orderApplicationService.createOrder(createOrderDto);

        // when
        var orderDto = orderApplicationService.getOrderById(orderId);

        // then
        assertThat(orderDto)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("customerId", createOrderDto.customerId())
                .hasFieldOrPropertyWithValue("price", createOrderDto.price())
                .hasFieldOrPropertyWithValue("status", OrderStatus.PENDING)
                .extracting(OrderDto::address)
                .hasFieldOrPropertyWithValue("street", createOrderDto.address().street())
                .hasFieldOrPropertyWithValue("postalCode", createOrderDto.address().postalCode())
                .hasFieldOrPropertyWithValue("city", createOrderDto.address().city())
                .hasFieldOrPropertyWithValue("houseNo", createOrderDto.address().houseNo());

        assertThat(orderDto.items()).hasSize(createOrderDto.items().size())
                .zipSatisfy(createOrderDto.items(), (dto, create) -> {
                    assertThat(dto.productId()).isEqualTo(create.productId());
                    assertThat(dto.price()).isEqualTo(create.price());
                    assertThat(dto.quantity()).isEqualTo(create.quantity());
                    assertThat(dto.totalPrice()).isEqualTo(create.totalPrice());
                });
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {
        // given
        var nonExistentOrderId = new OrderId(UUID.randomUUID());

        // expected
        assertThatThrownBy(() -> orderApplicationService.getOrderById(nonExistentOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(OrderNotFoundException.createExceptionMessage(nonExistentOrderId));
    }

    private CreateOrderDto getCreateOrderDto(UUID customerId) {
        var items = List.of(new CreateOrderItemDto(UUID.randomUUID(), 2, new BigDecimal("10.00"), new BigDecimal("20.00")),
                new CreateOrderItemDto(UUID.randomUUID(), 1, new BigDecimal("34.56"), new BigDecimal("34.56")));
        var address = new CreateOrderAddressDto("Małysza", "94-000", "Adasiowo", "12");
        return new CreateOrderDto(customerId, new BigDecimal("54.56"), items, address);
    }
}