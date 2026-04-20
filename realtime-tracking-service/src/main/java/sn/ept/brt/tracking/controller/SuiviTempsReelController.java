package sn.ept.brt.tracking.controller;

import sn.ept.brt.tracking.dto.ETAResponseDTO;
import sn.ept.brt.tracking.dto.PositionBusDTO;
import sn.ept.brt.tracking.model.SuiviTempsReel;
import sn.ept.brt.tracking.service.SuiviTempsReelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class SuiviTempsReelController {

    private final SuiviTempsReelService suiviService;

    @GetMapping("/buses")
    public List<SuiviTempsReel> getAllBusesPosition() {
        return suiviService.getAllSuivi();
    }

    @GetMapping("/buses/{busId}")
    public SuiviTempsReel getBusPosition(@PathVariable String busId) {
        return suiviService.getSuiviBus(busId);
    }

    @PostMapping("/buses/{busId}/position")
    public String updateBusPosition(@PathVariable String busId, @RequestBody PositionBusDTO position) {
        position.setBusId(busId);
        suiviService.mettreAJourPosition(position);
        return "Position mise à jour";
    }

    @GetMapping("/lines/{lineId}/eta")
    public ETAResponseDTO getETA(@PathVariable String lineId, @RequestParam String stationId) {
        return suiviService.calculerETA(lineId, stationId);
    }
}