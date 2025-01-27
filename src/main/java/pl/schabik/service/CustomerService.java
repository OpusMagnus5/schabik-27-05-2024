package pl.schabik.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.schabik.controller.CreateCustomerDto;
import pl.schabik.controller.CustomerDto;
import pl.schabik.exception.CustomerAlreadyExistsException;
import pl.schabik.exception.CustomerNotFoundException;
import pl.schabik.model.Customer;
import pl.schabik.repository.CustomerRepository;

import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerDto getCustomer(UUID id) {
        return customerRepository.findById(id)
                .map(customer -> new CustomerDto(id, customer.getFirstName(), customer.getLastName(), customer.getEmail()))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public UUID addCustomer(CreateCustomerDto customerDto) {
        if (customerRepository.existsByEmail(customerDto.email())) {
            throw new CustomerAlreadyExistsException(customerDto.email());
        }
        var customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName(customerDto.firstName());
        customer.setLastName(customerDto.lastName());
        customer.setEmail(customerDto.email());
        return customerRepository.save(customer).getId();
    }
}
