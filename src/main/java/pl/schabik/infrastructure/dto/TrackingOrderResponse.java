package pl.schabik.infrastructure.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TrackingOrderResponse(UUID id, String status, BigDecimal price) {
}