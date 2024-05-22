package pl.schabik.application.query.getorder;


import pl.schabik.domain.OrderAddress;
import pl.schabik.domain.OrderItem;

import java.util.List;

public class GetOrderQueryMapper {

    public static List<GetOrderItemQuery> convertToOrderItemsDto(List<OrderItem> items) {
        return items.stream()
                .map(item -> new GetOrderItemQuery(
                        item.getProductId(),
                        item.getQuantity().value(),
                        item.getPrice().amount(),
                        item.getTotalPrice().amount()
                )).toList();
    }

    public static GetOrderAddressQuery convertToOrderAddressDto(OrderAddress orderAddress) {
        if (orderAddress != null) {
            return new GetOrderAddressQuery(
                    orderAddress.getStreet(),
                    orderAddress.getPostalCode(),
                    orderAddress.getCity(),
                    orderAddress.getHouseNo()
            );
        }
        return null;
    }
}
