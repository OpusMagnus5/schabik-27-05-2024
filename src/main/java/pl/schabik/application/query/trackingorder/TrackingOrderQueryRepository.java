package pl.schabik.application.query.trackingorder;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.schabik.domain.OrderId;

interface TrackingOrderQueryRepository extends JpaRepository<TrackingOrderProjection, OrderId> {
}
