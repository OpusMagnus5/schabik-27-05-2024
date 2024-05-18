package pl.schabik.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Table(name = "orders")
@Entity
public class Order {

    @EmbeddedId
    private OrderId id;

    @NotNull
    private Instant createAt;

    @NotNull
    private Instant lastUpdateAt;

    @NotNull
    private UUID customerId;

    @NotNull
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress address;

    @NotNull
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private List<OrderItem> items;

    //For JPA
    protected Order() {
    }

    public Order(UUID customerId, Money price, List<OrderItem> items, OrderAddress address) {
        this.customerId = customerId;
        this.price = price;
        this.items = items;
        this.address = address;
        initialize();
    }

    private void initialize() {
        this.id = OrderId.newOne();
        this.createAt = Instant.now();
        this.lastUpdateAt = Instant.now();
        this.status = OrderStatus.PENDING;
        validatePrice(price, items);
        initializeBasketItems();
    }

    private void validatePrice(Money price, List<OrderItem> items) {
        Money itemsTotalCost = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(Money.ZERO, Money::add);

        if (!price.equals(itemsTotalCost)) {
            throw new OrderDomainException("Total order price: " + price
                    + " is different than order items total: " + itemsTotalCost);
        }
    }

    private void initializeBasketItems() {
        int itemNumber = 1;
        for (OrderItem item : items) {
            item.initializeBasketItem(this, itemNumber++);
        }
    }

    public void pay() {
        if (OrderStatus.PENDING != status) {
            throw new OrderDomainException("Order is not in correct state for pay operation");
        }
        lastUpdateAt = Instant.now();
        status = OrderStatus.PAID;
    }

    public boolean isPendingStatus() {
        return OrderStatus.PENDING == status;
    }

    public boolean isPaidStatus() {
        return OrderStatus.PAID == status;
    }

    public OrderId getId() {
        return id;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public Instant getLastUpdateAt() {
        return lastUpdateAt;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderAddress getAddress() {
        return address;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
