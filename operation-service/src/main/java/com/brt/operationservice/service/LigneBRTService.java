package com.brt.operationservice.service;

import com.brt.operationservice.entity.LigneBRT;
import com.brt.operationservice.entity.Station;
import com.brt.operationservice.repository.BusRepository;
import com.brt.operationservice.repository.LigneBRTRepository;
import com.brt.operationservice.repository.StationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class LigneBRTService {

    @Autowired private LigneBRTRepository ligneRepo;
    @Autowired private StationRepository stationRepo;
    @Autowired private BusRepository busRepo;

    public LigneBRT creerLigne(LigneBRT ligne) {
        if (ligneRepo.findByNom(ligne.getNom()).isPresent()) {
            throw new RuntimeException("Une ligne avec ce nom existe déjà.");
        }
        return ligneRepo.save(ligne);
    }

    public LigneBRT getLigneById(UUID id) {
        return ligneRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ligne introuvable : " + id));
    }

    public List<LigneBRT> getToutesLignes() {
        return ligneRepo.findAll();
    }

    public LigneBRT modifierLigne(UUID id, LigneBRT données) {
        LigneBRT ligne = getLigneById(id);
        ligne.setNom(données.getNom());
        ligne.setTerminusDepart(données.getTerminusDepart());
        ligne.setTerminusArrivee(données.getTerminusArrivee());
        ligne.setFrequence_minutes(données.getFrequence_minutes());
        return ligneRepo.save(ligne);
    }

    public void supprimerLigne(UUID id) {
        LigneBRT ligne = getLigneById(id);
        if (!ligne.getTrajets().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : des trajets sont liés à cette ligne.");
        }
        ligneRepo.deleteById(id);
    }

    // ─── GESTION DES STATIONS ────────────────────────────────

    /**
     * Ajoute une station à une ligne si elle n'y est pas déjà.
     */
    public LigneBRT ajouterStationALigne(UUID ligneId, UUID stationId) {
        LigneBRT ligne = getLigneById(ligneId);
        Station station = stationRepo.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station introuvable : " + stationId));

        boolean dejaPresente = ligne.getStations().stream()
                .anyMatch(s -> s.getId().equals(stationId));

        if (dejaPresente) {
            throw new RuntimeException("La station est déjà associée à cette ligne.");
        }

        station.setLigne(ligne);
        ligne.getStations().add(station);
        stationRepo.save(station);
        return ligneRepo.save(ligne);
    }

    public LigneBRT retirerStationDeLigne(UUID ligneId, String stationId) {
        LigneBRT ligne = getLigneById(ligneId);
        ligne.getStations().removeIf(s -> s.getId().equals(stationId));
        return ligneRepo.save(ligne);
    }

    /**
     * Calcule la durée totale estimée d'un trajet sur la ligne
     * en se basant sur : nb de stations × fréquence entre stations.
     * Hypothèse : temps moyen entre 2 stations = frequence_minutes / 2
     */
    public double calculerDureeLigne(UUID ligneId) {
        LigneBRT ligne = getLigneById(ligneId);
        int nbStations = ligne.getStations().size();

        if (nbStations < 2) {
            throw new RuntimeException("La ligne doit avoir au moins 2 stations.");
        }

        // Durée estimée = (nb intervalles) × (fréquence / 2) en minutes
        int nbIntervalles = nbStations - 1;
        double dureeMinutes = nbIntervalles * (ligne.getFrequence_minutes() / 2.0);
        return dureeMinutes;
    }

    /**
     * Calcule le nombre de bus nécessaires pour couvrir la ligne
     * selon la fréquence et la durée totale du trajet.
     * Formule : nbBus = (durée aller-retour) / fréquence
     */
    public int calculerNombreBusNecessaires(UUID ligneId) {
        LigneBRT ligne = getLigneById(ligneId);
        double dureeAllerMinutes = calculerDureeLigne(ligneId);
        double dureeAllerRetour = dureeAllerMinutes * 2;

        // Arrondi au supérieur pour garantir la couverture
        int nbBus = (int) Math.ceil(dureeAllerRetour / ligne.getFrequence_minutes());
        return nbBus;
    }

    /**
     * Vérifie si la ligne est opérationnelle :
     * - au moins 2 stations actives
     * - au moins 1 bus disponible affecté
     * - fréquence > 0
     */
    public boolean estOperationnelle(UUID ligneId) {
        LigneBRT ligne = getLigneById(ligneId);

        long stationsActives = ligne.getStations().stream()
                .filter(Station::isActif)
                .count();

        long busDisponibles = ligne.getBus().stream()
                .filter(b -> b.getStatut().equalsIgnoreCase("DISPONIBLE"))
                .count();

        return stationsActives >= 2
                && busDisponibles >= 1
                && ligne.getFrequence_minutes() > 0;
    }

    /**
     * Retourne les statistiques d'une ligne
     */
    public Map<String, Object> getStatistiquesLigne(UUID ligneId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("nbStations",         getLigneById(ligneId).getStations().size());
        stats.put("nbBusAffectes",      getLigneById(ligneId).getBus().size());
        stats.put("dureeEstimeeMin",    calculerDureeLigne(ligneId));
        stats.put("nbBusNecessaires",   calculerNombreBusNecessaires(ligneId));
        stats.put("estOperationnelle",  estOperationnelle(ligneId));
        return stats;
    }
}