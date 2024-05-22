package pl.schabik.infrastructure.dto;

import pl.schabik.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GetOrderResponse(
        UUID id,
        UUID customerId,
        BigDecimal price,
        OrderStatus status,
        List<GetOrderItemResponse> items,
        GetOrderAddressResponse address) {
}

