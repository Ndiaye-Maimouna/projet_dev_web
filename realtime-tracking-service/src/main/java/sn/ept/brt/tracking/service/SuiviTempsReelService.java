package sn.ept.brt.tracking.service;

import sn.ept.brt.tracking.client.FleetManagementServiceClient;
import sn.ept.brt.tracking.client.NotificationServiceClient;
import sn.ept.brt.tracking.client.OperationServiceClient;
import sn.ept.brt.tracking.dto.*;
import sn.ept.brt.tracking.event.BusArrivedAtStation;
import sn.ept.brt.tracking.event.BusDelayDetected;
import sn.ept.brt.tracking.model.Bus;
import sn.ept.brt.tracking.model.LigneBRT;
import sn.ept.brt.tracking.model.SuiviTempsReel;
import sn.ept.brt.tracking.repository.BusRepository;
import sn.ept.brt.tracking.repository.SuiviTempsReelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuiviTempsReelService {

    private final SuiviTempsReelRepository suiviRepository;
    private final BusRepository busRepository;
    private final EventPublisherService eventPublisher;
    private final OperationServiceClient operationClient;
    private final FleetManagementServiceClient fleetClient;
    private final NotificationServiceClient notificationClient;

    @Transactional
    public void mettreAJourPosition(PositionBusDTO positionDto) {
        BusDTO busInfo = fleetClient.getBusById(positionDto.getBusId());

        Bus bus = busRepository.findById(positionDto.getBusId())
                .orElseGet(() -> {
                    Bus newBus = new Bus();
                    newBus.setId(busInfo.getId());
                    newBus.setImmatriculation(busInfo.getImmatriculation());
                    newBus.setCapacite(busInfo.getCapacite());
                    newBus.setStatut(busInfo.getStatut());

                    if (busInfo.getLigneId() != null) {
                        LigneBRTDTO ligneInfo = operationClient.getLineById(busInfo.getLigneId());
                        LigneBRT ligne = new LigneBRT();
                        ligne.setId(ligneInfo.getId());
                        ligne.setNom(ligneInfo.getNom());
                        ligne.setTerminus_depart(ligneInfo.getTerminus_depart());
                        ligne.setTerminus_arrivee(ligneInfo.getTerminus_arrivee());
                        ligne.setFrequence_minutes(ligneInfo.getFrequence_minutes());
                        newBus.setLigne(ligne);
                    }
                    return busRepository.save(newBus);
                });

        SuiviTempsReel suivi = suiviRepository.findByBus(bus)
                .orElse(new SuiviTempsReel());

        if (suivi.getId() == null) {
            suivi.setId(bus.getId() + "_tracking");
            suivi.setBus(bus);
        }

        suivi.mettreAJourPosition(
                positionDto.getLatitude(),
                positionDto.getLongitude(),
                positionDto.getVitesse_kmh()
        );

        verifierArriveeStation(suivi, bus);
        verifierRetard(suivi, bus);

        suiviRepository.save(suivi);
        log.info("Position mise à jour pour bus {}: lat={}, lon={}",
                bus.getId(), positionDto.getLatitude(), positionDto.getLongitude());
    }

    private void verifierArriveeStation(SuiviTempsReel suivi, Bus bus) {
        if (bus.getLigne() == null) return;

        List<StationDTO> stations = operationClient.getAllStations();

        for (StationDTO stationDTO : stations) {
            double distance = calculerDistance(
                    suivi.getLatitude(), suivi.getLongitude(),
                    stationDTO.getLatitude(), stationDTO.getLongitude()
            );

            if (distance < 0.05) {
                BusArrivedAtStation event = new BusArrivedAtStation();
                event.setBusId(bus.getId());
                event.setStationId(stationDTO.getId());
                event.setLigneId(bus.getLigne().getId());
                event.setTimestamp(LocalDateTime.now());
                event.setRetard_minutes(suivi.getRetard_minutes());

                eventPublisher.publierArriveeBus(event);
                log.info("Bus {} arrivé à station {}", bus.getId(), stationDTO.getId());
                break;
            }
        }
    }

    private void verifierRetard(SuiviTempsReel suivi, Bus bus) {
        if (suivi.getRetard_minutes() > 10) {
            BusDelayDetected event = new BusDelayDetected();
            event.setBusId(bus.getId());
            event.setLigneId(bus.getLigne() != null ? bus.getLigne().getId() : null);
            event.setRetard_minutes(suivi.getRetard_minutes());
            event.setTimestamp(LocalDateTime.now());
            event.setCause("Trafic ou incident");

            eventPublisher.publierRetardBus(event);

            NotificationRequestDTO notif = new NotificationRequestDTO();
            notif.setTo("exploitant");
            notif.setType("APP");
            notif.setSubject("Retard détecté");
            notif.setMessage("Bus " + bus.getId() + " en retard de " + suivi.getRetard_minutes() + " minutes");
            notificationClient.sendNotification(notif);

            log.info("Retard détecté pour bus {}: {} minutes", bus.getId(), suivi.getRetard_minutes());
        }
    }

    private double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public SuiviTempsReel getSuiviBus(String busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus non trouvé: " + busId));
        return suiviRepository.findByBus(bus)
                .orElseThrow(() -> new RuntimeException("Suivi non trouvé pour bus " + busId));
    }

    public List<SuiviTempsReel> getAllSuivi() {
        return suiviRepository.findAll();
    }

    public ETAResponseDTO calculerETA(String ligneId, String stationId) {
        String nextBusId = operationClient.getNextBus(ligneId);
        ETAResponseDTO eta = new ETAResponseDTO();
        eta.setLigneId(ligneId);
        eta.setStationId(stationId);
        eta.setMinutes_restants(5);
        eta.setRetard_minutes(0);
        eta.setProchainBusId(nextBusId);
        return eta;
    }
}