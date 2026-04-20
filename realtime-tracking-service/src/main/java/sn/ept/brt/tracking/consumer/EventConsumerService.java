package sn.ept.brt.tracking.consumer;

import sn.ept.brt.tracking.client.FleetManagementServiceClient;
import sn.ept.brt.tracking.client.OperationServiceClient;
import sn.ept.brt.tracking.dto.BusDTO;
import sn.ept.brt.tracking.dto.LigneBRTDTO;
import sn.ept.brt.tracking.model.Bus;
import sn.ept.brt.tracking.model.LigneBRT;
import sn.ept.brt.tracking.model.SuiviTempsReel;
import sn.ept.brt.tracking.repository.BusRepository;
import sn.ept.brt.tracking.repository.SuiviTempsReelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumerService {

    private final BusRepository busRepository;
    private final SuiviTempsReelRepository suiviRepository;
    private final OperationServiceClient operationClient;
    private final FleetManagementServiceClient fleetClient;

    @KafkaListener(topics = "BusAssignedToLine")
    public void onBusAssignedToLine(String event) {
        log.info("BusAssignedToLine reçu: {}", event);
    }

    @KafkaListener(topics = "TicketValidated")
    public void onTicketValidated(String event) {
        log.info("TicketValidated reçu: {}", event);
    }
}