package sn.ept.brt.tracking.service;

import sn.ept.brt.tracking.event.BusArrivedAtStation;
import sn.ept.brt.tracking.event.BusDelayDetected;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publierArriveeBus(BusArrivedAtStation event) {
        kafkaTemplate.send("BusArrivedAtStation", event);
        log.info("Event BusArrivedAtStation publié pour bus {}", event.getBusId());
    }

    public void publierRetardBus(BusDelayDetected event) {
        kafkaTemplate.send("BusDelayDetected", event);
        log.info("Event BusDelayDetected publié pour bus {} avec retard {} min",
                event.getBusId(), event.getRetard_minutes());
    }
}