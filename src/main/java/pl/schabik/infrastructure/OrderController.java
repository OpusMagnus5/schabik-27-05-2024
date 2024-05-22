package pl.schabik.infrastructure;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.schabik.application.command.payorder.PayOrderCommand;
import pl.schabik.application.common.CommandHandlerExecutor;
import pl.schabik.application.query.getorder.GetOrderAddressQuery;
import pl.schabik.application.query.getorder.GetOrderItemQuery;
import pl.schabik.application.query.getorder.GetOrderQueryHandler;
import pl.schabik.application.query.trackingorder.TrackingOrderQueryHandler;
import pl.schabik.domain.OrderId;
import pl.schabik.infrastructure.dto.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CommandHandlerExecutor commandHandlerExecutor;
    private final GetOrderQueryHandler getOrderQueryHandler;
    private final TrackingOrderQueryHandler trackingOrderQueryHandler;

    public OrderController(CommandHandlerExecutor commandHandlerExecutor, GetOrderQueryHandler getOrderQueryHandler, TrackingOrderQueryHandler trackingOrderQueryHandler) {
        this.commandHandlerExecutor = commandHandlerExecutor;
        this.getOrderQueryHandler = getOrderQueryHandler;
        this.trackingOrderQueryHandler = trackingOrderQueryHandler;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        OrderId orderId = OrderId.newOne();
        var createOrderCommand = OrderApiMapper.mapToCreateOrderCommand(orderId, createOrderRequest);
        commandHandlerExecutor.execute(createOrderCommand);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderId.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> payOrder(@PathVariable OrderId id) {
        var payOrderCommand = new PayOrderCommand(id);
        commandHandlerExecutor.execute(payOrderCommand);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public GetOrderResponse getOrder(@PathVariable UUID id) {
        var orderQuery = getOrderQueryHandler.handle(new OrderId(id));

        return new GetOrderResponse(orderQuery.id(), orderQuery.customerId(), orderQuery.price(), orderQuery.status(),
                convertToOrderItemsDto(orderQuery.items()), convertToOrderAddressDto(orderQuery.address()));
    }

    private List<GetOrderItemResponse> convertToOrderItemsDto(List<GetOrderItemQuery> items) {
        return items.stream()
                .map(item -> new GetOrderItemResponse(
                        item.productId(),
                        item.quantity(),
                        item.price(),
                        item.totalPrice()
                )).toList();
    }

    private GetOrderAddressResponse convertToOrderAddressDto(GetOrderAddressQuery orderAddress) {
        if (orderAddress != null) {
            return new GetOrderAddressResponse(
                    orderAddress.street(),
                    orderAddress.postalCode(),
                    orderAddress.city(),
                    orderAddress.houseNo()
            );
        }
        return null;
    }

    @GetMapping("/{id}/tracking")
    public TrackingOrderResponse trackOrder(@PathVariable UUID id) {
        var projection = trackingOrderQueryHandler.getOrderById(id);
        return new TrackingOrderResponse(projection.getId().id(), projection.getStatus(), projection.getPrice());
    }
}
