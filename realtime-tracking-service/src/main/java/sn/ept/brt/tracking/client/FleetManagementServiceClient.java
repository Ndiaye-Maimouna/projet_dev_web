package sn.ept.brt.tracking.client;

import sn.ept.brt.tracking.dto.BusDTO;
import sn.ept.brt.tracking.dto.ConducteurDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "fleet-management-service", url = "http://localhost:8086")
public interface FleetManagementServiceClient {

    @GetMapping("/fleet/buses")
    List<BusDTO> getAllBuses();

    @GetMapping("/fleet/buses/{id}")
    BusDTO getBusById(@PathVariable("id") String id);

    @GetMapping("/fleet/conducteurs/{id}")
    ConducteurDTO getConducteurById(@PathVariable("id") String id);

    @GetMapping("/fleet/buses")
    List<BusDTO> getBusesByStatut(@RequestParam("statut") String statut);
}