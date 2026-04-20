package sn.ept.ticketing_payment_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sn.ept.ticketing_payment_service.dtos.LigneResponse;
import sn.ept.ticketing_payment_service.dtos.StationResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "OPERATION-SERVICE", url = "http://localhost:8083/api")
public interface OperationClient {

    @GetMapping("/lignes/{id}")
    LigneResponse getLigneById(@PathVariable UUID id);

    @GetMapping("/stations/ligne/{ligneId}")
    List<StationResponse> getStationsByLigne(@PathVariable UUID ligneId);

    @GetMapping("/stations/{stationId}")
    StationResponse getStation(@PathVariable UUID stationId);
}
