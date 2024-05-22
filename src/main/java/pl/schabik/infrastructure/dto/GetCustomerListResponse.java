package pl.schabik.infrastructure.dto;

import java.util.UUID;

public record GetCustomerListResponse(UUID id, String email) {
}
