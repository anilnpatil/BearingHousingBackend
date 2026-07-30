package TVS_SFL.BearingHousingBackend.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import TVS_SFL.BearingHousingBackend.constants.BearingHousingConstants;

@RestController
@RequestMapping(BearingHousingConstants.IMAGE_API_BASE_PATH)
@CrossOrigin(origins = "*", maxAge = 3600)
public class BearingHousingImageController {

    private static final Path ROOT_PATH =
            Path.of("C:/current working directroy/bearingHoushingPhotos");

    /**
     * Returns Production Data + Image URLs
     *
     * This endpoint still uses your Service from Part-1.
     */
    private final TVS_SFL.BearingHousingBackend.services.BearingHousingImageService service;

    public BearingHousingImageController(
            TVS_SFL.BearingHousingBackend.services.BearingHousingImageService service) {
        this.service = service;
    }

    @GetMapping("/report/{barcode}")
    public ResponseEntity<?> getData(
            @PathVariable String barcode) {

        return ResponseEntity.ok(
                service.getData(barcode));
    }

    /**
     * Generic File Endpoint
     *
     * Example
     *
     * /api/bearing-housing/image/
     * P06423094S260630002VSFLAD_P1_BK_S1-280726.jpeg
     *
     * or
     *
     * P06423094S260630002VSFLAD_P1_S1-280726.pdf
     */
        @GetMapping("/image/{fileName:.+}")
        public ResponseEntity<ByteArrayResource> getImage(
                @PathVariable String fileName)
                throws IOException {

        Path file = ROOT_PATH.resolve(fileName);

        System.out.println("Requested File : " + fileName);
        System.out.println("Resolved Path  : " + file.toAbsolutePath());
        System.out.println("Exists         : " + Files.exists(file));

        if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();
        }

        String contentType =
                Files.probeContentType(file);

        if (contentType == null) {

            if (fileName.toLowerCase().endsWith(".pdf")) {

                contentType = "application/pdf";

            } else if (fileName.toLowerCase().endsWith(".jpeg")
                    || fileName.toLowerCase().endsWith(".jpg")) {

                contentType = "image/jpeg";

            } else if (fileName.toLowerCase().endsWith(".png")) {

                contentType = "image/png";

            } else {

                contentType = "application/octet-stream";
            }
        }

        ByteArrayResource resource =
                new ByteArrayResource(
                        Files.readAllBytes(file));

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(Files.size(file))
                .header(
                        HttpHeaders.CACHE_CONTROL,
                        CacheControl.noCache().getHeaderValue())
                .body(resource);
    }

}