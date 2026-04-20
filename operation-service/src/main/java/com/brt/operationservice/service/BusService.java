package com.brt.operationservice.service;

import com.brt.operationservice.entity.Bus;
import com.brt.operationservice.entity.LigneBRT;
import com.brt.operationservice.entity.Trajet;
import com.brt.operationservice.repository.BusRepository;
import com.brt.operationservice.repository.LigneBRTRepository;
import com.brt.operationservice.repository.TrajetRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BusService {

    @Autowired private BusRepository busRepo;
    @Autowired private LigneBRTRepository ligneRepo;
    @Autowired private TrajetRepository trajetRepo;


    public Bus ajouterBus(Bus bus) {
        if (busRepo.findByImmatriculation(bus.getImmatriculation()).isPresent()) {
            throw new RuntimeException("Un bus avec cette immatriculation existe déjà.");
        }
        bus.setStatut("DISPONIBLE");
        return busRepo.save(bus);
    }

    public Bus getBusById(UUID id) {
        return busRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus introuvable : " + id));
    }

    public List<Bus> getTousLesBus() {
        return busRepo.findAll();
    }

    public List<Bus> getBusDisponibles() {
        return busRepo.findByStatut("DISPONIBLE");
    }

    public Bus modifierBus(UUID id, Bus données) {
        Bus bus = getBusById(id);
        bus.setImmatriculation(données.getImmatriculation());
        bus.setCapacite(données.getCapacite());
        return busRepo.save(bus);
    }

    public boolean verifierDisponibilite(UUID busId,
                                         LocalDateTime debut,
                                         LocalDateTime fin) {
        Bus bus = getBusById(busId);

        if (!bus.getStatut().equalsIgnoreCase("DISPONIBLE")) {
            return false;
        }

        boolean occupeSurCreneau = trajetRepo.findAll().stream()
                .filter(t -> t.getBus() != null
                        && t.getBus().getId().equals(busId))
                .filter(t -> t.getStatut().equals("PLANIFIE")
                        || t.getStatut().equals("EN_COURS"))
                .anyMatch(t ->
                        t.getHeureDepart().isBefore(fin) &&
                                t.getHeureArrivee().isAfter(debut));

        return !occupeSurCreneau;
    }

    /**
     * Affecte un bus à une ligne après vérification
     * de sa disponibilité et de sa capacité minimale.
     */
    public Bus affecterBusALigne(UUID busId, UUID ligneId) {
        Bus bus = getBusById(busId);
        LigneBRT ligne = ligneRepo.findById(ligneId)
                .orElseThrow(() -> new RuntimeException("Ligne introuvable : " + ligneId));

        if (!bus.getStatut().equalsIgnoreCase("DISPONIBLE")) {
            throw new RuntimeException("Le bus n'est pas disponible pour une affectation.");
        }

        final int CAPACITE_MINIMALE_BRT = 50;
        if (bus.getCapacite() < CAPACITE_MINIMALE_BRT) {
            throw new RuntimeException(
                    "Capacité insuffisante : minimum " + CAPACITE_MINIMALE_BRT + " passagers requis.");
        }

        ligne.getBus().add(bus);
        ligneRepo.save(ligne);

        bus.setStatut("AFFECTE");
        return busRepo.save(bus);
    }

    public Bus desaffecterBus(UUID busId, UUID ligneId) {
        Bus bus = getBusById(busId);
        LigneBRT ligne = ligneRepo.findById(ligneId)
                .orElseThrow(() -> new RuntimeException("Ligne introuvable"));

        ligne.getBus().removeIf(b -> b.getId().equals(busId));
        ligneRepo.save(ligne);

        bus.setStatut("DISPONIBLE");
        return busRepo.save(bus);
    }

    /**
     * Calcule le kilométrage total parcouru par un bus
     * à partir de ses trajets terminés.
     * Distance estimée = vitesse × durée de chaque trajet.
     */
    public double calculerKilometrageTotal(String busId) {
        final double VITESSE_MOYENNE_KMH = 40.0;

        return trajetRepo.findAll().stream()
                .filter(t -> t.getBus() != null && t.getBus().getId().equals(busId))
                .filter(t -> t.getStatut().equalsIgnoreCase("TERMINE"))
                .mapToDouble(t -> {
                    long dureeMinutes = Duration.between(
                            t.getHeureDepart(), t.getHeureArrivee()).toMinutes();
                    double dureeHeures = dureeMinutes / 60.0;
                    return VITESSE_MOYENNE_KMH * dureeHeures;
                })
                .sum();
    }

}