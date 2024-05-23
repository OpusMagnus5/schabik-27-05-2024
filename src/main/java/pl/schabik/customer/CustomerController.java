package pl.schabik.customer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id) {
        var customer = customerService.getCustomer(id);
        var customerResponse = CustomerApiMapper.mapToCustomerResponse(customer);
        return ResponseEntity.ok(customerResponse);
    }

    @PostMapping
    public UUID addCustomer(@RequestBody @Valid CreateCustomerRequest createCustomerRequest) {
        var createCustomerDto = CustomerApiMapper.mapToCreateCustomerDto(createCustomerRequest);
        return customerService.addCustomer(createCustomerDto);
    }
}
