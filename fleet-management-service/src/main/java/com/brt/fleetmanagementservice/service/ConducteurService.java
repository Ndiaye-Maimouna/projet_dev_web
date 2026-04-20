package com.brt.fleetmanagementservice.service;

import com.brt.fleetmanagementservice.entity.Affectation;
import com.brt.fleetmanagementservice.entity.Conducteur;
import com.brt.fleetmanagementservice.enums.ConducteurStatut;
import com.brt.fleetmanagementservice.repository.AffectationRepository;
import com.brt.fleetmanagementservice.repository.ConducteurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConducteurService {

    private final ConducteurRepository conducteurRepo;
    private final AffectationRepository affectationRepo;


    public Map<String, Object> getConducteurAvecDisponibilite(UUID id) {
        Conducteur conducteur = getConducteurById(id);

        // Affectation active en ce moment
        LocalDateTime maintenant = LocalDateTime.now();

        List<Affectation> affectationsActives = affectationRepo
                .findByConducteurId(id)
                .stream()
                .filter(a -> a.getStatut().equalsIgnoreCase("ACTIVE"))
                .filter(a -> a.getDateDebut().isBefore(maintenant)
                        && a.getDateFin().isAfter(maintenant))
                .collect(Collectors.toList());

        boolean disponible = conducteur.getStatut() == ConducteurStatut.DISPONIBLE
                && affectationsActives.isEmpty();

        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("conducteur",         conducteur);
        resultat.put("disponible",         disponible);
        resultat.put("affectationsActives", affectationsActives.size());
        resultat.put("prochainLibre",      calculerProchaineCree(id, maintenant));
        return resultat;
    }

    public List<Conducteur> getTousConducteurs() {
        return conducteurRepo.findAll();
    }

    public List<Conducteur> getConducteursDisponibles(LocalDateTime debut,
                                                      LocalDateTime fin) {
        if (fin == null || debut == null) {
            throw new RuntimeException("Les dates de début et de fin sont obligatoires.");
        }

        if (!fin.isAfter(debut)) {
            throw new RuntimeException("La date de fin doit être après la date de début.");
        }

        return conducteurRepo.findByStatut("DISPONIBLE")
                .stream()
                .filter(c -> !affectationRepo.existsConflitConducteur(c.getId(), debut, fin))
                .collect(Collectors.toList());
    }

    public Conducteur getConducteurById(UUID id) {
        return conducteurRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Conducteur introuvable : " + id));
    }

    public boolean estDisponible(UUID conducteurId,
                                 LocalDateTime debut,
                                 LocalDateTime fin) {
        Conducteur conducteur = getConducteurById(conducteurId);

        if (conducteur.getStatut() != ConducteurStatut.DISPONIBLE) {
            return false;
        }

        return !affectationRepo.existsConflitConducteur(conducteurId, debut, fin);
    }

    public Conducteur changerStatut(UUID conducteurId, String nouveauStatut) {
        Conducteur conducteur = getConducteurById(conducteurId);

        try {
            ConducteurStatut statut = ConducteurStatut.valueOf(nouveauStatut.toUpperCase());
            conducteur.setStatut(statut);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide : " + nouveauStatut);
        }

        return conducteurRepo.save(conducteur);
    }

    /**
     * Retourne la prochaine date à laquelle le conducteur sera libre,
     * en cherchant la fin de sa dernière affectation active.
     */
    private LocalDateTime calculerProchaineCree(UUID conducteurId,
                                                LocalDateTime depuis) {
        return affectationRepo.findByConducteurId(conducteurId)
                .stream()
                .filter(a -> a.getStatut().equalsIgnoreCase("ACTIVE"))
                .filter(a -> a.getDateFin().isAfter(depuis))
                .map(Affectation::getDateFin)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}