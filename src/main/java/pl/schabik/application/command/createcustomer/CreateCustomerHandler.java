package pl.schabik.application.command.createcustomer;

import org.springframework.stereotype.Component;
import pl.schabik.application.common.CommandHandler;
import pl.schabik.application.common.exception.CustomerAlreadyExistsException;
import pl.schabik.domain.Customer;
import pl.schabik.domain.CustomerRepository;

@Component
public class CreateCustomerHandler implements CommandHandler<CreateCustomerCommand> {

    private final CustomerRepository customerRepository;

    public CreateCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void handle(CreateCustomerCommand command) {
        if (customerRepository.existsByEmail(command.email())) {
            throw new CustomerAlreadyExistsException(command.email());
        }
        var customer = new Customer(command.id(), command.firstName(), command.lastName(), command.email());
        customerRepository.save(customer);
    }
}
