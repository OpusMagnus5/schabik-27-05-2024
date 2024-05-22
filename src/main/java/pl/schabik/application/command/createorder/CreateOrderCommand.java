package pl.schabik.application.command.createorder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import pl.schabik.domain.CustomerId;
import pl.schabik.domain.OrderId;
import pl.schabik.application.common.Command;

import java.math.BigDecimal;
import java.util.List;


public record CreateOrderCommand(
        @NotNull OrderId id,
        @NotNull CustomerId customerId,
        @NotNull @Min(0) BigDecimal price,
        @Valid @NotNull List<CreateOrderItemDto> items,
        @Valid CreateOrderAddressDto address
) implements Command {
}

