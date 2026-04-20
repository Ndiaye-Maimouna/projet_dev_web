package com.brt.operationservice.service;

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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrajetService {

    @Autowired private TrajetRepository trajetRepo;
    @Autowired private LigneBRTRepository ligneRepo;
    @Autowired private BusRepository busRepo;

    public Trajet planifierTrajet(Trajet trajet) {
        // Vérification : l'heure d'arrivée doit être après le départ
        if (!trajet.getHeureArrivee().isAfter(trajet.getHeureDepart())) {
            throw new RuntimeException("L'heure d'arrivée doit être postérieure au départ.");
        }

        trajet.setStatut("PLANIFIE");
        return trajetRepo.save(trajet);
    }

    public Trajet getTrajetById(UUID id) {
        return trajetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajet introuvable : " + id));
    }

    public List<Trajet> getTousLesTrajets() {
        return trajetRepo.findAll();
    }

    public List<Trajet> getTrajetsByLigne(UUID ligneId) {
        return trajetRepo.findByLigneId(ligneId);
    }


    public List<Trajet> getTrajetsByPlage(LocalDateTime debut, LocalDateTime fin) {
        if (fin.isBefore(debut)) {
            throw new RuntimeException("La date de fin doit être après la date de début.");
        }
        return trajetRepo.findByHeureDepartBetween(debut, fin);
    }

    /**
     * Transitions autorisées :
     * PLANIFIE → EN_COURS → TERMINE
     * PLANIFIE → ANNULE
     * EN_COURS → ANNULE
     */
    public Trajet mettreAJourStatut(UUID id, String nouveauStatut) {
        Trajet trajet = getTrajetById(id);
        String actuel = trajet.getStatut();

        Map<String, List<String>> transitions = Map.of(
                "PLANIFIE",  List.of("EN_COURS", "ANNULE"),
                "EN_COURS",  List.of("TERMINE",  "ANNULE"),
                "TERMINE",   List.of(),
                "ANNULE",    List.of()
        );

        if (!transitions.get(actuel).contains(nouveauStatut)) {
            throw new RuntimeException(
                    "Transition interdite : " + actuel + " → " + nouveauStatut);
        }

        trajet.setStatut(nouveauStatut);

        if (nouveauStatut.equals("EN_COURS")) {
            trajet.setHeureDepart(LocalDateTime.now());
        }
        if (nouveauStatut.equals("TERMINE")) {
            trajet.setHeureArrivee(LocalDateTime.now());
        }

        return trajetRepo.save(trajet);
    }
    /**
     * Calcule la durée réelle ou planifiée d'un trajet.
     */
    public Duration calculerDuree(UUID id) {
        Trajet trajet = getTrajetById(id);
        return Duration.between(trajet.getHeureDepart(), trajet.getHeureArrivee());
    }

    /**
     * Calcule le retard en minutes d'un trajet EN_COURS ou TERMINE.
     * Retard = heure_arrivee_reelle - heure_arrivee_planifiee
     * Retourne 0 si le trajet est à l'heure ou en avance.
     */
    public long calculerRetard(UUID id) {
        Trajet trajet = getTrajetById(id);

        if (!trajet.getStatut().equals("TERMINE") &&
                !trajet.getStatut().equals("EN_COURS")) {
            throw new RuntimeException("Retard calculable uniquement pour un trajet EN_COURS ou TERMINE.");
        }

        LocalDateTime heureReelle = LocalDateTime.now();
        long retardMinutes = Duration.between(
                trajet.getHeureArrivee(), heureReelle).toMinutes();

        return Math.max(0, retardMinutes);
    }

}