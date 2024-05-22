package pl.schabik.application.query.trackingorder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.schabik.application.common.exception.OrderNotFoundException;
import pl.schabik.domain.OrderId;

import java.util.UUID;

@Component
public class TrackingOrderQueryHandler {

    private final TrackingOrderQueryRepository trackingOrderQueryRepository;

    public TrackingOrderQueryHandler(TrackingOrderQueryRepository trackingOrderQueryRepository) {
        this.trackingOrderQueryRepository = trackingOrderQueryRepository;
    }

    @Transactional(readOnly = true)
    public TrackingOrderProjection getOrderById(UUID orderId) {
        return trackingOrderQueryRepository.findById(new OrderId(orderId)).orElseThrow(() -> new OrderNotFoundException(new OrderId(orderId)));
    }
}
