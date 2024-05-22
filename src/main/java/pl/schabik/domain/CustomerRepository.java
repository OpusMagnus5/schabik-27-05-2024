package pl.schabik.domain;

import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId id);

    boolean existsByEmail(String email);
}