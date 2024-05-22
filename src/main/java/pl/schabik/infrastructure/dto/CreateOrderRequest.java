package pl.schabik.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public record CreateOrderRequest(
        @NotNull UUID customerId,
        @NotNull @Min(0) BigDecimal price,
        @Valid @NotNull List<OrderItemRequest> items,
        @Valid OrderAddressRequest address
) {

    public record OrderItemRequest(
            @NotNull UUID productId,
            @NotNull @Min(0) Integer quantity,
            @NotNull @Min(0) BigDecimal price,
            @NotNull @Min(0) BigDecimal totalPrice
    ) {
    }

    public record OrderAddressRequest(
            @NotBlank String street,
            @NotBlank String postalCode,
            @NotBlank String city,
            @NotBlank String houseNo) {
    }
}


