package pl.schabik.application.query.getorder;

import java.math.BigDecimal;
import java.util.UUID;

public record GetOrderItemQuery(
        UUID productId,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice
) {
}
