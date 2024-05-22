package pl.schabik.application.query.trackingorder;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import pl.schabik.domain.OrderId;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Immutable
public class TrackingOrderProjection {

    @Id
    private OrderId id;

    private String status;

    private BigDecimal price;

    public OrderId getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }


    public BigDecimal getPrice() {
        return price;
    }

}
