package pl.schabik.customer;

import java.util.UUID;

public record CustomerResponse(UUID id, String firstName, String lastName, String email) {
}
