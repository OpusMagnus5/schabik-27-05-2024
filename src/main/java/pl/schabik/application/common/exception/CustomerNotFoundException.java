package pl.schabik.application.common.exception;

import pl.schabik.domain.CustomerId;

public class CustomerNotFoundException extends RuntimeException {

    public static String createExceptionMessage(CustomerId customerId) {
        return String.format("Could not find customer with customerId: %s", customerId.id());
    }

    public CustomerNotFoundException(final CustomerId customerId) {
        super(createExceptionMessage(customerId));
    }
}
