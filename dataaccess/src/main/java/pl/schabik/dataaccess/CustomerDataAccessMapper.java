package pl.schabik.dataaccess;

import pl.schabik.domain.Customer;

public class CustomerDataAccessMapper {

    public static CustomerEntity customerToCustomerEntity(Customer customer) {
        var customerEntity = new CustomerEntity();
        customerEntity.setId(customer.getId());
        customerEntity.setFirstName(customer.getFirstName());
        customerEntity.setLastName(customer.getLastName());
        customerEntity.setEmail(customer.getEmail());
        return customerEntity;
    }

    public static Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(customerEntity.getId(), customerEntity.getFirstName(),
                customerEntity.getLastName(), customerEntity.getEmail());
    }
}