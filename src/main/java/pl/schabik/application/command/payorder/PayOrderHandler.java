package pl.schabik.application.command.payorder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.schabik.application.common.CommandHandler;
import pl.schabik.application.common.exception.OrderNotFoundException;
import pl.schabik.domain.OrderRepository;

@Component
public class PayOrderHandler implements CommandHandler<PayOrderCommand> {

    private final OrderRepository orderRepository;

    public PayOrderHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void handle(PayOrderCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));
        order.pay();
        orderRepository.save(order);
    }
}
