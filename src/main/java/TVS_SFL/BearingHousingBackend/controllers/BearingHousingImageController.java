package TVS_SFL.BearingHousingBackend.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import TVS_SFL.BearingHousingBackend.constants.BearingHousingConstants;
import TVS_SFL.BearingHousingBackend.dto.BearingHousingDataToImageResponse;
import TVS_SFL.BearingHousingBackend.services.BearingHousingImageService;

import lombok.RequiredArgsConstructor;

// REST controller for bearing housing production data, images and PDFs. 
@RestController
@RequestMapping(BearingHousingConstants.IMAGE_API_BASE_PATH)
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class BearingHousingImageController {

    private static final Logger logger = LoggerFactory.getLogger(BearingHousingImageController.class);

    private final BearingHousingImageService imageService;

    /** GET /api/bearing-housing/report/{barcode}
     * Returns production data with image/PDF URLs
     */
    @GetMapping("/report/{barcode}")
    public ResponseEntity<BearingHousingDataToImageResponse> getReport(
            @PathVariable String barcode) {
        try {
            return ResponseEntity.ok(imageService.getData(barcode));
        } catch (RuntimeException ex) {
            logger.error("Error retrieving report for barcode: {}", barcode, ex);
            throw ex;
        }
    }
    /**  GET /api/bearing-housing/image/{fileName}
     *   Download production image (JPEG, PNG, GIF, BMP, WebP)
     */
    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String fileName) throws IOException {
        try {
            return imageService.getImageFile(fileName);
        } catch (IOException ex) {
            logger.error("Error serving image: {}", fileName, ex);
            throw ex;
        }
    }
    /** GET /api/bearing-housing/pdf/{fileName}
     *  Download production PDF report (inline preview)     */
    @GetMapping("/pdf/{fileName:.+}")
    public ResponseEntity<Resource> getPdf(
            @PathVariable String fileName) throws IOException {
        try {
            return imageService.getPdfFile(fileName);
        } catch (IOException ex) {
            logger.error("Error serving PDF: {}", fileName, ex);
            throw ex;
        }
    }
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleCorsPreflightOptions() {
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Max-Age", "3600")
                .build();
    }
}