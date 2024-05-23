package pl.schabik.order.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.schabik.order.domain.OrderDomainService;

@Configuration
class OrderSpringConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }
}
