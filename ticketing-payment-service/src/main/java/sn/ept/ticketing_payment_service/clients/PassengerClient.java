package sn.ept.ticketing_payment_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sn.ept.ticketing_payment_service.dtos.PassengerResponse;

import java.util.UUID;

@FeignClient(name = "PASSENGER-SERVICE", url = "http://localhost:8082/api")
public interface PassengerClient {

    @GetMapping("/passengers/{id}")
    PassengerResponse getPassager(@PathVariable("id") UUID id);

    @GetMapping("/passengers/by-user/{userId}")
    PassengerResponse getByUserId(@PathVariable("userId") UUID userId);
}
