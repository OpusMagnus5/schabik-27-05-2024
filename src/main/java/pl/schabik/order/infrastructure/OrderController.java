package pl.schabik.order.infrastructure;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.schabik.order.application.OrderApplicationService;
import pl.schabik.order.domain.vo.OrderId;
import pl.schabik.order.infrastructure.dto.CreateOrderRequest;
import pl.schabik.order.infrastructure.dto.GetOrderResponse;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public UUID createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        var createOrderDto = OrderApiMapper.mapToDto(createOrderRequest);
        return orderApplicationService.createOrder(createOrderDto).id();
    }

    @GetMapping("/{id}")
    public GetOrderResponse getOrder(@PathVariable UUID id) {
        var orderDto = orderApplicationService.getOrderById(new OrderId(id));
        return OrderApiMapper.mapToGetOrderResponse(orderDto);
    }
}
