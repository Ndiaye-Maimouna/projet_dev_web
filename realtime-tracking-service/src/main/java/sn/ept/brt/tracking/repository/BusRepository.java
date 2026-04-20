package sn.ept.brt.tracking.repository;

import sn.ept.brt.tracking.model.Bus;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BusRepository {

    private final Map<String, Bus> busMap = new ConcurrentHashMap<>();

    public Optional<Bus> findById(String id) {
        return Optional.ofNullable(busMap.get(id));
    }

    public List<Bus> findByStatut(String statut) {
        return busMap.values().stream()
                .filter(b -> statut.equals(b.getStatut()))
                .toList();
    }

    public Bus save(Bus bus) {
        busMap.put(bus.getId(), bus);
        return bus;
    }

    public List<Bus> findAll() {
        return new ArrayList<>(busMap.values());
    }
}