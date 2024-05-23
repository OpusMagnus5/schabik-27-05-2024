package pl.schabik.order.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.schabik.order.application.dto.CreateOrderDto;
import pl.schabik.order.application.dto.OrderDto;
import pl.schabik.order.application.exception.CustomerNotFoundException;
import pl.schabik.order.application.exception.OrderNotFoundException;
import pl.schabik.order.application.replication.CustomerProjectionService;
import pl.schabik.order.domain.Order;
import pl.schabik.order.domain.OrderDomainService;
import pl.schabik.order.domain.OrderRepository;
import pl.schabik.order.domain.vo.Money;
import pl.schabik.order.domain.vo.OrderId;

import java.util.UUID;

@Service
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final CustomerProjectionService customerProjectionService;
    private final OrderDomainService orderDomainService;

    public OrderApplicationService(OrderRepository orderRepository,
                                   CustomerProjectionService customerProjectionService,
                                   OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.customerProjectionService = customerProjectionService;
        this.orderDomainService = orderDomainService;
    }

    public OrderId createOrder(CreateOrderDto createOrderDto) {
        validateCustomerExists(createOrderDto.customerId());
        var items = OrderMapper.convertToCreateOrderItems(createOrderDto.items());
        var orderAddress = OrderMapper.convertToCreateOrderAddress(createOrderDto.address());

        var order = new Order(createOrderDto.customerId(), new Money(createOrderDto.price()),
                items, orderAddress);

        return orderRepository.save(order).getId();
    }

    //TODO
    @Transactional
    public void pay(OrderId orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        orderDomainService.pay(order);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(OrderId orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDto(orderId.id(), order.getCustomerId(), order.getPrice().amount(), order.getStatus(),
                OrderMapper.convertToOrderItemsDto(order.getItems()), OrderMapper.convertToOrderAddressDto(order.getAddress()));
    }

    private void validateCustomerExists(UUID customerId) {
        if (!customerProjectionService.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
