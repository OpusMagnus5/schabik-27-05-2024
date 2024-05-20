package pl.schabik.usecase.getcustomer;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public static String createExceptionMessage(UUID id) {
        return String.format("Could not find customer with customerId: %s", id);
    }

    public CustomerNotFoundException(final UUID id) {
        super(createExceptionMessage(id));
    }
}