package pl.schabik.application.command.createcustomer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.schabik.domain.CustomerId;
import pl.schabik.application.common.Command;

public record CreateCustomerCommand(
        @NotNull CustomerId id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email
) implements Command {
}

