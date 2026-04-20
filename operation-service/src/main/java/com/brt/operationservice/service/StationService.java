package com.brt.operationservice.service;

import com.brt.operationservice.dto.request.StationRequestDTO;
import com.brt.operationservice.entity.LigneBRT;
import com.brt.operationservice.entity.Station;
import com.brt.operationservice.entity.Trajet;
import com.brt.operationservice.repository.LigneBRTRepository;
import com.brt.operationservice.repository.StationRepository;
import com.brt.operationservice.repository.TrajetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {

    @Autowired private StationRepository stationRepo;
    @Autowired private TrajetRepository trajetRepo;
    @Autowired private LigneBRTRepository ligneBRTRepo;

    // ─── CRUD ───────────────────────────────────────────────

    public Station creerStation(StationRequestDTO dto) {

        LigneBRT ligne = ligneBRTRepo.findById(dto.getLigneId())
                .orElseThrow(() -> new RuntimeException("Ligne introuvable"));

        Station station = new Station();
        station.setNom(dto.getNom());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setActif(true);
        station.setLigne(ligne);

        return stationRepo.save(station);
    }

    public Station getStationById(UUID id) {
        return stationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Station introuvable : " + id));
    }

    public List<Station> getToutesStations() {
        return stationRepo.findAll();
    }

    public List<Station> getStationsActives() {
        return stationRepo.findByActifTrue();
    }

    public List<Station> getStationsByLigne(UUID ligneId) {
        return stationRepo.findByLigneId(ligneId);
    }

    public Station modifierStation(UUID id, Station données) {
        Station station = getStationById(id);
        station.setNom(données.getNom());
        station.setLatitude(données.getLatitude());
        station.setLongitude(données.getLongitude());
        return stationRepo.save(station);
    }

    public void desactiverStation(UUID id) {
        Station station = getStationById(id);
        station.setActif(false);
        stationRepo.save(station);
    }

    public void activerStation(UUID id) {
        Station station = getStationById(id);
        station.setActif(true);
        stationRepo.save(station);
    }

    // ─── CALCULS MÉTIER ──────────────────────────────────────

    /**
     * Calcule la distance en km entre deux stations
     * via la formule de Haversine.
     */
    public double calculerDistanceEntre(UUID stationAId, UUID stationBId) {
        Station a = getStationById(stationAId);
        Station b = getStationById(stationBId);

        final int RAYON_TERRE_KM = 6371;

        double dLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());

        double sinDLat = Math.sin(dLat / 2);
        double sinDLon = Math.sin(dLon / 2);

        double haversine = sinDLat * sinDLat
                + Math.cos(Math.toRadians(a.getLatitude()))
                * Math.cos(Math.toRadians(b.getLatitude()))
                * sinDLon * sinDLon;

        double c = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return RAYON_TERRE_KM * c;
    }

    /**
     * Estime le temps de trajet entre deux stations
     * en supposant une vitesse moyenne de 40 km/h pour un BRT.
     */
    public double estimerTempsTrajet(UUID stationAId, UUID stationBId) {
        double distanceKm = calculerDistanceEntre(stationAId, stationBId);
        final double VITESSE_MOYENNE_BRT = 40.0; // km/h
        double dureeHeures = distanceKm / VITESSE_MOYENNE_BRT;
        return dureeHeures * 60; // en minutes
    }

    /**
     * Retourne les prochains trajets au départ d'une station
     * dans les 60 prochaines minutes.
     */
    public List<Trajet> getProchainsBus(String stationId) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dans1h = maintenant.plusHours(1);

        return trajetRepo
                .findByStationDepartIdAndHeureDepartBetween(stationId, maintenant, dans1h)
                .stream()
                .filter(t -> t.getStatut().equalsIgnoreCase("PLANIFIE")
                        || t.getStatut().equalsIgnoreCase("EN_COURS"))
                .sorted(Comparator.comparing(Trajet::getHeureDepart))
                .collect(Collectors.toList());
    }

    /**
     * Retourne les stations triées par distance croissante
     * par rapport à une position GPS donnée.
     */
    public List<Station> getStationsProches(double latitude, double longitude) {
        return stationRepo.findByActifTrue().stream()
                .sorted(Comparator.comparingDouble(s ->
                        calculerDistanceGPS(latitude, longitude, s.getLatitude(), s.getLongitude())))
                .collect(Collectors.toList());
    }

    /**
     * Méthode utilitaire interne : distance GPS avec Haversine.
     */
    private double calculerDistanceGPS(double lat1, double lon1,
                                       double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}