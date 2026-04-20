package com.brt.fleetmanagementservice.service;

import com.brt.fleetmanagementservice.entity.Bus;
import com.brt.fleetmanagementservice.repository.BusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepo;

    public List<Bus> getTousLesBus() {
        return busRepo.findAll();
    }

    public Bus getBusById(UUID id) {
        return busRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus introuvable : " + id));
    }


    public boolean estDisponible(UUID busId) {
        Bus bus = getBusById(busId);
        return bus.getStatut().equalsIgnoreCase("DISPONIBLE");
    }

    public Bus changerStatut(UUID busId, String nouveauStatut) {
        Bus bus = getBusById(busId);
        bus.setStatut(nouveauStatut);
        return busRepo.save(bus);
    }

    public Bus ajouterBus(Bus bus) {
        busRepo.findByImmatriculation(bus.getImmatriculation())
                .ifPresent(b -> { throw new RuntimeException(
                        "Immatriculation déjà enregistrée : " + bus.getImmatriculation()); });
        bus.setStatut("DISPONIBLE");
        return busRepo.save(bus);
    }

    public void mettreAJourKilometrage(UUID busId, double kmAjoutes) {
        Bus bus = getBusById(busId);
        bus.setKilometrage(bus.getKilometrage() + kmAjoutes);
        busRepo.save(bus);
    }
}