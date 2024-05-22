package pl.schabik.infrastructure;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.schabik.domain.Customer;
import pl.schabik.domain.CustomerId;
import pl.schabik.domain.CustomerRepository;
import pl.schabik.infrastructure.dto.GetCustomerListResponse;

import java.util.List;
import java.util.Optional;

@Repository
public class SqlCustomerRepository implements CustomerRepository {

    private final CustomerRepositoryJpa customerRepositoryJpa;

    public SqlCustomerRepository(CustomerRepositoryJpa customerRepositoryJpa) {
        this.customerRepositoryJpa = customerRepositoryJpa;
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepositoryJpa.save(customer);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return customerRepositoryJpa.findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerRepositoryJpa.existsByEmail(email);
    }


    public List<GetCustomerListResponse> findAll() {
        return customerRepositoryJpa.findAllDto();
    }
}

@Repository
interface CustomerRepositoryJpa extends CrudRepository<Customer, CustomerId> {

    boolean existsByEmail(String email);

    @Query("SELECT new pl.schabik.infrastructure.dto.GetCustomerListResponse(c.id.customerId , c.email) FROM Customer c")
    List<GetCustomerListResponse> findAllDto();
}
