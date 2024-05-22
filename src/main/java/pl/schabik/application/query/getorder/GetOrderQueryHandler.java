package pl.schabik.application.query.getorder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.schabik.application.common.exception.OrderNotFoundException;
import pl.schabik.domain.OrderId;
import pl.schabik.domain.OrderRepository;

@Component
public class GetOrderQueryHandler {

    private final OrderRepository orderRepository;

    public GetOrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public GetOrderQuery handle(OrderId orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new GetOrderQuery(orderId.id(), order.getCustomer().getId().id(), order.getPrice().amount(), order.getStatus(),
                GetOrderQueryMapper.convertToOrderItemsDto(order.getItems()), GetOrderQueryMapper.convertToOrderAddressDto(order.getAddress()));
    }
}
