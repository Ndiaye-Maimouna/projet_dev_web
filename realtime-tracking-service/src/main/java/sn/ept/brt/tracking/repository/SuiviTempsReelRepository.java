package sn.ept.brt.tracking.repository;

import sn.ept.brt.tracking.model.SuiviTempsReel;
import sn.ept.brt.tracking.model.Bus;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SuiviTempsReelRepository {

    private final Map<String, SuiviTempsReel> suiviMap = new ConcurrentHashMap<>();

    public Optional<SuiviTempsReel> findById(String id) {
        return Optional.ofNullable(suiviMap.get(id));
    }

    public Optional<SuiviTempsReel> findByBus(Bus bus) {
        return suiviMap.values().stream()
                .filter(s -> s.getBus() != null && s.getBus().getId().equals(bus.getId()))
                .findFirst();
    }

    public SuiviTempsReel save(SuiviTempsReel suivi) {
        suiviMap.put(suivi.getId(), suivi);
        return suivi;
    }

    public List<SuiviTempsReel> findAll() {
        return new ArrayList<>(suiviMap.values());
    }
}