package pl.schabik.application.query.getorder;

import pl.schabik.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GetOrderQuery(
        UUID id,
        UUID customerId,
        BigDecimal price,
        OrderStatus status,
        List<GetOrderItemQuery> items,
        GetOrderAddressQuery address) {
}

