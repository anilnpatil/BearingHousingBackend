package TVS_SFL.BearingHousingBackend.services.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import TVS_SFL.BearingHousingBackend.services.BarcodeScanWebSocketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BarcodeScanWebSocketServiceImpl
        implements BarcodeScanWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcastBarcode(String barcode) {

        messagingTemplate.convertAndSend(
                "/topic/barcode-scan",
                barcode
        );
    }
}