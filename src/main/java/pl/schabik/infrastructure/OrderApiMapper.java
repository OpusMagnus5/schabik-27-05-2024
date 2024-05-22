package pl.schabik.infrastructure;


import pl.schabik.domain.CustomerId;
import pl.schabik.domain.OrderId;
import pl.schabik.infrastructure.dto.CreateOrderRequest;
import pl.schabik.application.command.createorder.CreateOrderAddressDto;
import pl.schabik.application.command.createorder.CreateOrderCommand;
import pl.schabik.application.command.createorder.CreateOrderItemDto;

import java.util.stream.Collectors;

public class OrderApiMapper {

    public static CreateOrderCommand mapToCreateOrderCommand(OrderId id, CreateOrderRequest createOrderRequest) {
        var itemsDto = createOrderRequest.items().stream()
                .map(OrderApiMapper::mapToItem)
                .collect(Collectors.toList());

        return new CreateOrderCommand(
                id,
                new CustomerId(createOrderRequest.customerId()),
                createOrderRequest.price(),
                itemsDto,
                mapToAddress(createOrderRequest.address())
        );
    }

    private static CreateOrderItemDto mapToItem(CreateOrderRequest.OrderItemRequest request) {
        return new CreateOrderItemDto(
                request.productId(),
                request.quantity(),
                request.price(),
                request.totalPrice()
        );
    }

    private static CreateOrderAddressDto mapToAddress(CreateOrderRequest.OrderAddressRequest request) {
        return new CreateOrderAddressDto(
                request.street(),
                request.postalCode(),
                request.city(),
                request.houseNo()
        );
    }
}