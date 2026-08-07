// package TVS_SFL.BearingHousingBackend.controllers;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;

// import org.springframework.core.io.ByteArrayResource;
// import org.springframework.core.io.Resource;
// import org.springframework.http.CacheControl;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import TVS_SFL.BearingHousingBackend.constants.BearingHousingConstants;

// @RestController
// @RequestMapping(BearingHousingConstants.IMAGE_API_BASE_PATH)
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class BearingHousingImageController {

//     private static final Path ROOT_PATH =
//             Path.of("C:/current working directroy/bearingHoushingPhotos");

//     private final TVS_SFL.BearingHousingBackend.services.BearingHousingImageService service;

//     public BearingHousingImageController(
//             TVS_SFL.BearingHousingBackend.services.BearingHousingImageService service) {
//         this.service = service;
//     }

//     @GetMapping("/report/{barcode}")
//     public ResponseEntity<?> getData(
//             @PathVariable String barcode) {

//         return ResponseEntity.ok(
//                 service.getData(barcode));
//     }

//     /**
//      * Endpoint for IMAGES only (JPEG, PNG, etc.)
//      * 
//      * Example:
//      * /api/bearing-housing/image/P06423094S260630002VSFLAD_P1_BK_S1-280726.jpeg
//      */
//     @GetMapping("/image/{fileName:.+}")
//     public ResponseEntity<Resource> getImage(
//             @PathVariable String fileName)
//             throws IOException {

//         // Validate file extension
//         if (!isImageFile(fileName)) {
//             return ResponseEntity.badRequest().build();
//         }

//         Path file = ROOT_PATH.resolve(fileName);

//         if (!Files.exists(file)) {
//             return ResponseEntity.notFound().build();
//         }

//         String contentType = Files.probeContentType(file);
//         if (contentType == null) {
//             if (fileName.toLowerCase().endsWith(".jpeg") 
//                     || fileName.toLowerCase().endsWith(".jpg")) {
//                 contentType = "image/jpeg";
//             } else if (fileName.toLowerCase().endsWith(".png")) {
//                 contentType = "image/png";
//             } else {
//                 contentType = "application/octet-stream";
//             }
//         }

//         byte[] fileContent = Files.readAllBytes(file);
//         ByteArrayResource resource = new ByteArrayResource(fileContent);

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.parseMediaType(contentType));
//         headers.setCacheControl(CacheControl.noCache().getHeaderValue());
//         headers.setContentLength(fileContent.length);

//         return ResponseEntity
//                 .ok()
//                 .headers(headers)
//                 .body(resource);
//     }

//     /**
//      * Endpoint for PDFs only
//      * 
//      * Example:
//      * /api/bearing-housing/pdf/P06423094S260630002VSFLAD_P1_S1-280726.pdf
//      */
//     @GetMapping("/pdf/{fileName:.+}")
//     public ResponseEntity<Resource> getPdf(
//             @PathVariable String fileName)
//             throws IOException {

//         // Validate file extension
//         if (!fileName.toLowerCase().endsWith(".pdf")) {
//             return ResponseEntity.badRequest().build();
//         }

//         Path file = ROOT_PATH.resolve(fileName);

//         if (!Files.exists(file)) {
//             return ResponseEntity.notFound().build();
//         }

//         byte[] fileContent = Files.readAllBytes(file);
//         ByteArrayResource resource = new ByteArrayResource(fileContent);

//         HttpHeaders headers = new HttpHeaders();
        
//         // Set proper headers for PDF
//         headers.setContentType(MediaType.APPLICATION_PDF);
//         headers.setContentLength(fileContent.length);
//         headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        
//         // Set Content-Disposition for inline viewing
//         headers.set(HttpHeaders.CONTENT_DISPOSITION, 
//                 "inline; filename=\"" + fileName + "\"");
        
//         // Additional headers for better PDF compatibility
//         headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
//         headers.set(HttpHeaders.CONTENT_ENCODING, "identity");
        
//         // CORS headers
//         headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//         headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS");
//         headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, 
//                 "Content-Type, Accept, Range, Content-Disposition");
//         headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, 
//                 "Content-Type, Content-Disposition, Content-Length");

//         return ResponseEntity
//                 .ok()
//                 .headers(headers)
//                 .body(resource);
//     }

//     /**
//      * Validate if file is an image
//      */
//     private boolean isImageFile(String fileName) {
//         String lower = fileName.toLowerCase();
//         return lower.endsWith(".jpeg") 
//                 || lower.endsWith(".jpg") 
//                 || lower.endsWith(".png") 
//                 || lower.endsWith(".gif") 
//                 || lower.endsWith(".bmp") 
//                 || lower.endsWith(".webp");
//     }

//     /**
//      * OPTIONS endpoint for CORS preflight
//      */
//     @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
//     public ResponseEntity<Void> handleOptions() {
//         return ResponseEntity
//                 .ok()
//                 .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
//                 .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS")
//                 .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*")
//                 .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600")
//                 .build();
//     }
// }



package TVS_SFL.BearingHousingBackend.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import TVS_SFL.BearingHousingBackend.constants.BearingHousingConstants;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.repositories.BearingHousingImageRepository;
import TVS_SFL.BearingHousingBackend.services.BearingHousingImageService;

@RestController
@RequestMapping(BearingHousingConstants.IMAGE_API_BASE_PATH)
@CrossOrigin(origins = "*", maxAge = 3600)
public class BearingHousingImageController {

    private static final Path ARCHIVE_ROOT =
            Path.of("C:/current working directroy/bearingHoushingPhotos");
    private static final Path LIVE_IMAGE_ROOT =
            Path.of("C:/BearingHousingImages/IV-4");
    private static final Path LIVE_GRAPH_ROOT =
            Path.of("C:/BearingHousingGraphs");
    private static final int ARCHIVE_DAYS = 90;
    private static final String SHIFT_PREFIX = "shift";

    private final BearingHousingImageService service;
    private final BearingHousingImageRepository repository;

    public BearingHousingImageController(
            BearingHousingImageService service,
            BearingHousingImageRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping("/report/{barcode}")
    public ResponseEntity<?> getData(@PathVariable String barcode) {
        return ResponseEntity.ok(service.getData(barcode));
    }

    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {
        // Validate image extension
        if (!isImageFile(fileName)) {
            return ResponseEntity.badRequest().build();
        }
        return serveFile(fileName, getContentTypeForImage(fileName));
    }

    @GetMapping("/pdf/{fileName:.+}")
    public ResponseEntity<Resource> getPdf(@PathVariable String fileName) throws IOException {
        // Validate PDF extension
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest().build();
        }
        return servePdfFile(fileName);
    }

    private ResponseEntity<Resource> serveFile(String fileName, String contentType) throws IOException {
        Path file = resolveFilePath(fileName);
        if (!Files.exists(file)) {
            System.err.println("File not found: " + file.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }

        byte[] content = Files.readAllBytes(file);
        ByteArrayResource resource = new ByteArrayResource(content);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setContentLength(content.length);

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    private ResponseEntity<Resource> servePdfFile(String fileName) throws IOException {
        Path file = resolveFilePath(fileName);
        System.out.println("PDF Requested: " + fileName);
        System.out.println("Resolved Path: " + file.toAbsolutePath());
        System.out.println("File exists: " + Files.exists(file));

        if (!Files.exists(file)) {
            System.err.println("PDF file not found: " + file.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }

        byte[] content = Files.readAllBytes(file);
        ByteArrayResource resource = new ByteArrayResource(content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(content.length);
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
        // CORS headers
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition, Content-Length");

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    private Path resolveFilePath(String fileName) {
        Path liveFile = findInLiveRoots(fileName);
        if (liveFile != null) {
            return liveFile;
        }

        String barcode = extractBarcode(fileName);
        if (barcode == null) {
            return null;
        }

        BearingHousingProductionData data = null;
        try {
            data = repository.findByBarcode(barcode);
        } catch (Exception ex) {
            System.err.println("Unable to resolve barcode metadata for " + barcode + ": " + ex.getMessage());
        }

        if (data == null || data.getProductionDateTime() == null || data.getShift() == null) {
            return null;
        }

        if (isArchived(data.getProductionDateTime())) {
            Path archiveFile = buildArchivePath(data, fileName);
            if (Files.exists(archiveFile)) {
                System.out.println("Resolved archived file path: " + archiveFile.toAbsolutePath());
                return archiveFile;
            }
        }

        return null;
    }

    private boolean isArchived(LocalDateTime productionDateTime) {
        return productionDateTime.toLocalDate().isBefore(LocalDate.now().minusDays(ARCHIVE_DAYS));
    }

    private Path buildArchivePath(BearingHousingProductionData data, String fileName) {
        LocalDate productionDate = data.getProductionDateTime().toLocalDate();
        String shiftFolder = SHIFT_PREFIX + data.getShift();
        String year = String.format("%04d", productionDate.getYear());
        String month = String.format("%02d", productionDate.getMonthValue());
        String day = String.format("%02d", productionDate.getDayOfMonth());

        return ARCHIVE_ROOT.resolve(shiftFolder)
                .resolve(year)
                .resolve(month)
                .resolve(day)
                .resolve(data.getBarcode())
                .resolve(fileName);
    }

    private Path findInLiveRoots(String fileName) {
        Path imageFile = LIVE_IMAGE_ROOT.resolve(fileName);
        if (Files.exists(imageFile)) {
            return imageFile;
        }

        Path graphFile = LIVE_GRAPH_ROOT.resolve(fileName);
        if (Files.exists(graphFile)) {
            return graphFile;
        }

        return null;
    }

    private String extractBarcode(String fileName) {
        int delimiter = fileName.indexOf('_');
        if (delimiter <= 0) {
            return null;
        }
        return fileName.substring(0, delimiter);
    }

    private boolean isImageFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png") ||
               lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp");
    }

    private String getContentTypeForImage(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpeg") || lower.endsWith(".jpg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*")
                .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600")
                .build();
    }
}