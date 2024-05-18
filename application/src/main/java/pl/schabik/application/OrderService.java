package pl.schabik.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.schabik.application.dto.CreateOrderDto;
import pl.schabik.application.dto.OrderDto;
import pl.schabik.application.exception.CustomerNotFoundException;
import pl.schabik.application.exception.OrderNotFoundException;
import pl.schabik.domain.*;

import java.util.UUID;

import static pl.schabik.application.OrderMapper.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public OrderId createOrder(CreateOrderDto createOrderDto) {
        var customer = findCustomerById(createOrderDto.customerId());
        var items = convertToCreateOrderItems(createOrderDto.items());
        var orderAddress = convertToCreateOrderAddress(createOrderDto.address());

        var order = new Order(customer.getId(), new Money(createOrderDto.price()),
                items, orderAddress);

        return orderRepository.save(order).getId();
    }

    //TODO
    @Transactional
    public void pay(OrderId orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        order.pay();
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(OrderId orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDto(orderId.id(), order.getCustomerId(), order.getPrice().amount(), order.getStatus(),
                convertToOrderItemsDto(order.getItems()), convertToOrderAddressDto(order.getAddress()));
    }

    private Customer findCustomerById(UUID customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
