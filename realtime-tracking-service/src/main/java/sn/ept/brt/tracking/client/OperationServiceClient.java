package sn.ept.brt.tracking.client;

import sn.ept.brt.tracking.dto.LigneBRTDTO;
import sn.ept.brt.tracking.dto.StationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "operation-service", url = "http://localhost:8083")
public interface OperationServiceClient {

    @GetMapping("/lines")
    List<LigneBRTDTO> getAllLines();

    @GetMapping("/lines/{id}")
    LigneBRTDTO getLineById(@PathVariable("id") String id);

    @GetMapping("/stations")
    List<StationDTO> getAllStations();

    @GetMapping("/stations/{id}")
    StationDTO getStationById(@PathVariable("id") String id);

    @GetMapping("/lines/{id}/next-bus")
    String getNextBus(@PathVariable("id") String lineId);
}