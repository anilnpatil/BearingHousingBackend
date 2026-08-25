package TVS_SFL.BearingHousingBackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TVS_SFL.BearingHousingBackend.constants.BearingHousingConstants;
import TVS_SFL.BearingHousingBackend.dto.BarcodeScanEvent;
import TVS_SFL.BearingHousingBackend.services.BarcodeScanWebSocketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(BearingHousingConstants.IMAGE_API_BASE_PATH)
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class BarcodeScanWebSocketController {

    private static final Logger logger =
            LoggerFactory.getLogger(BarcodeScanWebSocketController.class);

    private final BarcodeScanWebSocketService barcodeScanWebSocketService;
    
     // POST /api/bearing-housing/barcode-scan
     // Receives a barcode scanned from the mobile device and broadcasts it to all connected Angular clients  through WebSocket.
     
    @PostMapping("/barcode-scan")
    public ResponseEntity<Void> barcodeScanned(
            @RequestBody BarcodeScanEvent event) {

        if (event == null ||
                event.getBarcode() == null ||
                event.getBarcode().isBlank()) {

            logger.warn("Received empty barcode scan event");

            return ResponseEntity.badRequest().build();
        }

        String barcode = event.getBarcode().trim();

        logger.info("Barcode scanned from client: {}", barcode);

        barcodeScanWebSocketService.broadcastBarcode(barcode);

        return ResponseEntity.ok().build();
    }
}