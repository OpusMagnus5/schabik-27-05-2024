package pl.schabik.application.command.payorder;

import jakarta.validation.constraints.NotNull;
import pl.schabik.domain.OrderId;
import pl.schabik.application.common.Command;

public record PayOrderCommand(
        @NotNull OrderId orderId
) implements Command {
}
