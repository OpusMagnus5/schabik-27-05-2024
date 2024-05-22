package pl.schabik.application.command.createorder;

import org.springframework.stereotype.Component;
import pl.schabik.application.common.CommandHandler;
import pl.schabik.application.common.exception.CustomerNotFoundException;
import pl.schabik.domain.*;

@Component
public class CreateOrderHandler implements CommandHandler<CreateOrderCommand> {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public CreateOrderHandler(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(CreateOrderCommand command) {
        var customer = findCustomerById(command.customerId());
        var order = mapToOrder(customer, command);
        orderRepository.save(order);
    }

    private Customer findCustomerById(CustomerId customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public Order mapToOrder(Customer customer, CreateOrderCommand command) {
        var items = command.items().stream()
                .map(this::mapToItem)
                .toList();

        var address = mapToAddress(command.address());

        return new Order(
                command.id(),
                customer,
                new Money(command.price()),
                items,
                address
        );
    }

    private OrderItem mapToItem(CreateOrderItemDto dto) {
        return new OrderItem(
                dto.productId(),
                new Money(dto.price()),
                new Quantity(dto.quantity()),
                new Money(dto.totalPrice())
        );
    }

    private OrderAddress mapToAddress(CreateOrderAddressDto dto) {
        return new OrderAddress(
                dto.street(),
                dto.postalCode(),
                dto.city(),
                dto.houseNo()
        );
    }
}
