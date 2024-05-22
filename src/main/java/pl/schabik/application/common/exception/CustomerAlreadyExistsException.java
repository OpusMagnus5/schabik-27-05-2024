package pl.schabik.application.common.exception;

public class CustomerAlreadyExistsException extends RuntimeException {

    public static String createExceptionMessage(String email) {
        return String.format("Customer with email: %s already exists", email);
    }

    public CustomerAlreadyExistsException(String email) {
        super(createExceptionMessage(email));
    }
}