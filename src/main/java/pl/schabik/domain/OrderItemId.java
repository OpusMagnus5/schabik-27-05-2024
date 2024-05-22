package pl.schabik.domain;

import java.io.Serializable;
import java.util.Objects;


public class OrderItemId implements Serializable {

    private Integer id;
    private Order order;

    public OrderItemId(Integer id, Order order) {
        this.id = id;
        this.order = order;
    }

    public OrderItemId() {
    }

    public Integer getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }
}
