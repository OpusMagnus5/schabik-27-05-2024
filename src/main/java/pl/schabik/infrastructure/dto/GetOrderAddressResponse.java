package pl.schabik.infrastructure.dto;

public record GetOrderAddressResponse(
        String street,
        String postalCode,
        String city,
        String houseNo) {
}
