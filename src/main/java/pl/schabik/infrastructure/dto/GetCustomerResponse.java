package pl.schabik.infrastructure.dto;

import java.util.UUID;

public record GetCustomerResponse(UUID id, String firstName, String lastName, String email) {
}
