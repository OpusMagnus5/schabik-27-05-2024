package pl.schabik.domain;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class OrderId {

    public static OrderId newOne() {
        return new OrderId(UUID.randomUUID());
    }

    private UUID orderId;

    public OrderId(UUID uuid) {
        this.orderId = uuid;
    }

    protected OrderId() {
    }

    public UUID id() {
        return orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId1 = (OrderId) o;
        return Objects.equals(orderId, orderId1.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }
}
