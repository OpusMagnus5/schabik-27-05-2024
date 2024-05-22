package pl.schabik.application;

import pl.schabik.domain.Order;
import pl.schabik.domain.OrderId;
import pl.schabik.domain.OrderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> store = new HashMap<>();

    @Override
    public Order save(Order order) {
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(store.get(id));
    }

    void deleteAll() {
        store.clear();
    }
}