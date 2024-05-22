package pl.schabik.domain;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CustomerId {

    public static CustomerId newOne() {
        return new CustomerId(UUID.randomUUID());
    }

    private UUID customerId;

    public CustomerId(UUID uuid) {
        this.customerId = uuid;
    }

    protected CustomerId() {
    }

    public UUID id() {
        return customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerId);
    }
}
