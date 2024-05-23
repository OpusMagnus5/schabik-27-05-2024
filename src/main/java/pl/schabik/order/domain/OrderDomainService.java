package pl.schabik.order.domain;

public class OrderDomainService {

    public void pay(Order order) {
        order.pay();
        //Gdy logika domenowa większa niż agregat
        //logowanie
        //tworzenie Domain Event
    }
}


