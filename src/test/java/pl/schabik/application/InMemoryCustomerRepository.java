package pl.schabik.application;

import pl.schabik.domain.Customer;
import pl.schabik.domain.CustomerId;
import pl.schabik.domain.CustomerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<CustomerId, Customer> store = new HashMap<>();

    @Override
    public Customer save(Customer customer) {
        store.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    void deleteAll() {
        store.clear();
    }
}
