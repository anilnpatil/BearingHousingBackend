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
            Path.of("C:/BearingHousingOldFiles");
    private static final Path LIVE_IMAGE_ROOT =
            Path.of("C:/BearingHousingImages/IV-4");
    private static final Path LIVE_GRAPH_ROOT =
            Path.of("C:/BearingHousingGraphs");
    private static final int ARCHIVE_DAYS = 90;
    private static final String SHIFT_PREFIX = "Shift";
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
        if (file == null || !Files.exists(file)) {
            System.err.println("File not found: " + (file == null ? fileName : file.toAbsolutePath()));
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
        System.out.println("Resolved Path: " + (file == null ? "null" : file.toAbsolutePath()));
        System.out.println("File exists: " + (file == null ? false : Files.exists(file)));

        if (file == null || !Files.exists(file)) {
            System.err.println("PDF file not found: " + (file == null ? fileName : file.toAbsolutePath()));
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
            return null;
        }

        Path liveFile = findInLiveRoots(fileName);
        return liveFile != null ? liveFile : null;
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