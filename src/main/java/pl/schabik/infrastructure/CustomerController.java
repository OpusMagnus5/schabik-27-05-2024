package pl.schabik.infrastructure;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.schabik.application.command.createcustomer.CreateCustomerCommand;
import pl.schabik.application.common.CommandHandlerExecutor;
import pl.schabik.application.common.exception.CustomerNotFoundException;
import pl.schabik.domain.CustomerId;
import pl.schabik.infrastructure.dto.CreateCustomerRequest;
import pl.schabik.infrastructure.dto.GetCustomerListResponse;
import pl.schabik.infrastructure.dto.GetCustomerResponse;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CommandHandlerExecutor commandHandlerExecutor;
    private final SqlCustomerRepository sqlCustomerRepository;

    public CustomerController(CommandHandlerExecutor commandHandlerExecutor, SqlCustomerRepository customerRepository) {
        this.commandHandlerExecutor = commandHandlerExecutor;
        this.sqlCustomerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<Void> addCustomer(@RequestBody @Valid CreateCustomerRequest request) {
        var customerId = CustomerId.newOne();
        var createCustomerCommand = new CreateCustomerCommand(customerId, request.firstName(), request.lastName(), request.email());
        commandHandlerExecutor.execute(createCustomerCommand);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customerId.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCustomerResponse> getCustomer(@PathVariable UUID id) {
        var customerResponse = sqlCustomerRepository.findById(new CustomerId(id)).map(customer -> new GetCustomerResponse(customer.getId().id(),
                        customer.getFirstName(), customer.getLastName(), customer.getEmail()))
                .orElseThrow(() -> new CustomerNotFoundException(new CustomerId(id)));
        return ResponseEntity.ok(customerResponse);
    }

    @GetMapping
    public List<GetCustomerListResponse> getAllCustomers() {
        return sqlCustomerRepository.findAll();
    }
}

