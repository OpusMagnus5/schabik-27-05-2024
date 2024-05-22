package pl.schabik.application.query.getorder;

public record GetOrderAddressQuery(
        String street,
        String postalCode,
        String city,
        String houseNo) {
}
