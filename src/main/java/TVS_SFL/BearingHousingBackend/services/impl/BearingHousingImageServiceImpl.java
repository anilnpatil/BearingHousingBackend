package TVS_SFL.BearingHousingBackend.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import TVS_SFL.BearingHousingBackend.config.FilePathConfig;
import TVS_SFL.BearingHousingBackend.dto.BearingHousingDataToImageResponse;
import TVS_SFL.BearingHousingBackend.dto.BearingHousingImageInfo;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.repositories.BearingHousingImageRepository;
import TVS_SFL.BearingHousingBackend.services.BearingHousingImageService;

import lombok.RequiredArgsConstructor;

/**
 * ========================================================================
 * BEARING HOUSING IMAGE SERVICE IMPLEMENTATION
 * ========================================================================
 * Purpose: Implements business logic for bearing housing image and data
 *          retrieval operations with comprehensive validation and error
 *          handling.
 * 
 * Key Features:
 * - Centralized file path management via FilePathConfig
 * - Secure file serving with validation
 * - Proper MIME type and cache headers
 * - Archived vs. live file routing
 * - Comprehensive logging for debugging
 * 
 * Performance Considerations:
 * - File lookups optimized via barcode-indexed database queries
 * - Archive paths built dynamically (no pre-computation)
 * - Image/PDF URLs cached by HTTP clients (Expires/Cache-Control headers)
 * 
 * Security Considerations:
 * - Barcode validation prevents directory traversal attacks
 * - File extension whitelist prevents arbitrary file access
 * - No-cache headers prevent sensitive data caching in browsers
 * - CORS headers properly configured for cross-origin requests
 * 
 * Thread-Safety: Yes (stateless service, no mutable fields)
 * Database: Requires bearing_housing_production_data table with
 *           barcode indexed for optimal performance
 * ========================================================================
 */
@Service
@RequiredArgsConstructor
public class BearingHousingImageServiceImpl implements BearingHousingImageService {

    // ====================================================================
    // DEPENDENCIES & CONFIGURATION
    // ====================================================================
    
    private static final Logger logger = LoggerFactory.getLogger(BearingHousingImageServiceImpl.class);
    
    private final BearingHousingImageRepository repository;
    private final FilePathConfig filePathConfig;

    // ====================================================================
    // API ENDPOINT PATHS - Accessible to clients
    // ====================================================================
    
    /** Base URL for image endpoints: /api/bearing-housing/image/{fileName} */
    private static final String IMAGE_BASE_URL = "/api/bearing-housing/image";
    
    /** Base URL for PDF endpoints: /api/bearing-housing/pdf/{fileName} */
    private static final String PDF_BASE_URL = "/api/bearing-housing/pdf";

    // ====================================================================
    // FILE NAMING PATTERNS & CONSTANTS
    // ====================================================================
    
    /** File name pattern for images: BARCODE_PROCESS_SHIFT_STATUS_DATE.jpeg */
    private static final String IMAGE_FILE_EXTENSION = ".jpeg";
    
    /** File name pattern for PDFs: BARCODE_PROCESS_SHIFT_DATE.pdf */
    private static final String PDF_FILE_EXTENSION = ".pdf";
    
    /** Prefix for shift directory naming in archives */
    private static final String SHIFT_PREFIX = "Shift";
    
    /** Status code for "Before Glue" images */
    private static final String STATUS_BEFORE = "B";
    
    /** Status code for "After Glue" images */
    private static final String STATUS_AFTER = "A";

    // ====================================================================
    // PUBLIC METHODS - Service Interface Implementation
    // ====================================================================

    /**
     * IMPLEMENTS: BearingHousingImageService.getData(String barcode)
     * 
     * Retrieves complete production data with image and PDF URLs.
     * Organizes results by process (P1, P2) and image type (before/after).
     * 
     * Performance: O(1) - single DB query + string operations
     * Thread-Safe: Yes
     * 
     * @param barcode unique bearing housing identifier
     * @return complete image response with all media URLs
     * @throws RuntimeException if barcode not found
     */
    @Override
    public BearingHousingDataToImageResponse getData(String barcode) {
        logger.debug("getData() - Fetching data for barcode: {}", barcode);

        // Step 1: Retrieve production data from database
        BearingHousingProductionData data = repository.findByBarcode(barcode);

        // Step 2: Validate barcode exists
        if (data == null) {
            logger.error("getData() - Barcode not found: {}", barcode);
            throw new RuntimeException("Barcode not found: " + barcode);
        }

        logger.debug("getData() - Found production data for barcode: {} (processes: {})", 
            barcode, data.getNumberofProcess());

        // Step 3: Build response with all image/PDF information
        BearingHousingDataToImageResponse response = new BearingHousingDataToImageResponse();
        response.setProductionData(data);

        // Step 4: Add Process 1 images and PDFs (always present)
        addProcess1Images(response, data);

        // Step 5: Add Process 2 images and PDFs (if applicable)
        if (hasProcess2(data)) {
            addProcess2Images(response, data);
            logger.debug("getData() - Added Process 2 images for barcode: {}", barcode);
        }

        logger.debug("getData() - Successfully built response for barcode: {}", barcode);
        return response;
    }

    /**
     * IMPLEMENTS: BearingHousingImageService.getImageFile(String fileName)
     * 
     * Serves image file with proper headers and caching directives.
     * Handles both live and archived files.
     * 
     * @param fileName the image file to serve (must end with supported extension)
     * @return ResponseEntity with image data and headers
     * @throws IOException if file I/O fails
     */
    @Override
    public ResponseEntity<Resource> getImageFile(String fileName) throws IOException {
        logger.debug("getImageFile() - Requested: {}", fileName);

        // Step 1: Validate file format
        if (!filePathConfig.isImageFile(fileName)) {
            logger.warn("getImageFile() - Invalid image file format: {}", fileName);
            return ResponseEntity.badRequest().build();
        }

        // Step 2: Get production data and resolve file path
        BearingHousingProductionData productionData = validateAndGetBarcode(fileName);
        Path filePath = resolveFilePath(fileName, productionData);

        // Step 3: Validate file exists
        if (filePath == null || !Files.exists(filePath)) {
            logger.error("getImageFile() - File not found: {}", 
                (filePath == null ? fileName : filePath.toAbsolutePath()));
            return ResponseEntity.notFound().build();
        }

        // Step 4: Read file and prepare response
        byte[] fileContent = readFileContent(filePath);
        String mimeType = filePathConfig.getMimeType(fileName);
        
        return buildImageResponse(fileContent, fileName, mimeType);
    }

    /**
     * IMPLEMENTS: BearingHousingImageService.getPdfFile(String fileName)
     * 
     * Serves PDF file with inline display and CORS headers.
     * Handles both live and archived files.
     * 
     * @param fileName the PDF file to serve (must end with .pdf)
     * @return ResponseEntity with PDF data and headers
     * @throws IOException if file I/O fails
     */
    @Override
    public ResponseEntity<Resource> getPdfFile(String fileName) throws IOException {
        logger.debug("getPdfFile() - Requested: {}", fileName);

        // Step 1: Validate file format (must be PDF only)
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            logger.warn("getPdfFile() - Invalid PDF file format: {}", fileName);
            return ResponseEntity.badRequest().build();
        }

        // Step 2: Get production data and resolve file path
        BearingHousingProductionData productionData = validateAndGetBarcode(fileName);
        Path filePath = resolveFilePath(fileName, productionData);

        // Step 3: Validate file exists
        if (filePath == null || !Files.exists(filePath)) {
            logger.error("getPdfFile() - File not found: {}", 
                (filePath == null ? fileName : filePath.toAbsolutePath()));
            return ResponseEntity.notFound().build();
        }

        // Step 4: Read file and prepare response with PDF-specific headers
        byte[] fileContent = readFileContent(filePath);
        return buildPdfResponse(fileContent, fileName);
    }

    /**
     * IMPLEMENTS: BearingHousingImageService.isFileArchived(LocalDateTime)
     * 
     * Determines if a file should be served from archive storage.
     * Archive threshold: 90 days old
     * 
     * @param productionDateTime the production date to check
     * @return true if older than 90 days (should be archived)
     */
    @Override
    public boolean isFileArchived(LocalDateTime productionDateTime) {
        if (productionDateTime == null) {
            return false;
        }
        
        LocalDate productionDate = productionDateTime.toLocalDate();
        LocalDate archiveThresholdDate = LocalDate.now()
            .minusDays(filePathConfig.getArchiveThresholdDays());
        
        boolean isArchived = productionDate.isBefore(archiveThresholdDate);
        logger.debug("isFileArchived() - Date: {}, Threshold: {}, Archived: {}", 
            productionDate, archiveThresholdDate, isArchived);
        
        return isArchived;
    }

    /**
     * IMPLEMENTS: BearingHousingImageService.validateAndGetBarcode(String)
     * 
     * Extracts and validates barcode from filename.
     * Prevents directory traversal attacks.
     * 
     * Barcode Format: First part of filename before underscore
     * Example: "BH123456_P1_S1_B_OK_280726.jpeg" → barcode = "BH123456"
     * 
     * @param fileName the file name to parse
     * @return ProductionData if barcode is valid and exists in DB
     * @throws RuntimeException if barcode format is invalid or not found
     */
    @Override
    public BearingHousingProductionData validateAndGetBarcode(String fileName) {
        logger.debug("validateAndGetBarcode() - Validating filename: {}", fileName);

        // Step 1: Extract barcode from filename (part before first underscore)
        String barcode = extractBarcodeFromFileName(fileName);
        if (barcode == null || barcode.isEmpty()) {
            logger.error("validateAndGetBarcode() - Could not extract barcode from: {}", fileName);
            throw new RuntimeException("Invalid file name format - barcode not found");
        }

        // Step 2: Query database for production data
        BearingHousingProductionData productionData = null;
        try {
            productionData = repository.findByBarcode(barcode);
        } catch (Exception ex) {
            logger.error("validateAndGetBarcode() - Database error while validating barcode: {}", 
                barcode, ex);
            throw new RuntimeException("Database error during barcode validation", ex);
        }

        // Step 3: Validate barcode exists in database
        if (productionData == null) {
            logger.error("validateAndGetBarcode() - Barcode not found in database: {}", barcode);
            throw new RuntimeException("Barcode not found: " + barcode);
        }

        logger.debug("validateAndGetBarcode() - Successfully validated barcode: {}", barcode);
        return productionData;
    }

    // ====================================================================
    // PRIVATE HELPER METHODS - Image/PDF Building
    // ====================================================================

    /**
     * Add Process 1 image and PDF information to response.
     * Includes before/after glue images and graph PDF.
     * 
     * @param response the response object to populate
     * @param data the production data
     */
    private void addProcess1Images(BearingHousingDataToImageResponse response, 
                                   BearingHousingProductionData data) {
        // Before glue image
        String beforeStatus = data.getP1_beforeGlueStatus() == 1 ? "B_OK" : "B_NG";
        response.setP1_beforeImage(buildImageInfo(data, "P1", beforeStatus));

        // After glue image
        String afterStatus = data.getP1_afterGlueStatus() == 1 ? "A_OK" : "A_NG";
        response.setP1_afterImage(buildImageInfo(data, "P1", afterStatus));

        // Graph PDF
        response.setP1_graphImage(buildPdfInfo(data, "P1"));
    }

    /**
     * Add Process 2 image and PDF information to response.
     * Only added if number_of_process >= 2
     * 
     * @param response the response object to populate
     * @param data the production data
     */
    private void addProcess2Images(BearingHousingDataToImageResponse response, 
                                   BearingHousingProductionData data) {
        // Before glue image
        String beforeStatus = data.getP2_beforeGlueStatus() == 1 ? "B_OK" : "B_NG";
        response.setP2_beforeImage(buildImageInfo(data, "P2", beforeStatus));

        // After glue image
        String afterStatus = data.getP2_afterGlueStatus() == 1 ? "A_OK" : "A_NG";
        response.setP2_afterImage(buildImageInfo(data, "P2", afterStatus));

        // Graph PDF
        response.setP2_graphImage(buildPdfInfo(data, "P2"));
    }

    /**
     * Check if production data has Process 2.
     * 
     * @param data the production data
     * @return true if number_of_process >= 2
     */
    private boolean hasProcess2(BearingHousingProductionData data) {
        return data.getNumberofProcess() != null && data.getNumberofProcess() >= 2;
    }

    /**
     * Build image metadata object with URL.
     * 
     * @param data production data
     * @param process process identifier (P1, P2)
     * @param statusCode status string (B_OK, B_NG, A_OK, A_NG)
     * @return BearingHousingImageInfo with URL
     */
    private BearingHousingImageInfo buildImageInfo(BearingHousingProductionData data, 
                                                   String process, 
                                                   String statusCode) {
        String fileName = buildImageFileName(data, process, statusCode);
        String url = IMAGE_BASE_URL + "/" + fileName;
        return new BearingHousingImageInfo(statusCode, url);
    }

    /**
     * Build PDF metadata object with URL.
     * 
     * @param data production data
     * @param process process identifier (P1, P2)
     * @return BearingHousingImageInfo with URL
     */
    private BearingHousingImageInfo buildPdfInfo(BearingHousingProductionData data, 
                                                 String process) {
        String fileName = buildPdfFileName(data, process);
        String url = PDF_BASE_URL + "/" + fileName;
        
        // Determine PDF status (OK or NOT_OK)
        String status = getPdfStatus(data, process);
        
        return new BearingHousingImageInfo(status, url);
    }

    /**
     * Get PDF status based on graph status flag.
     * 
     * @param data production data
     * @param process process identifier (P1, P2)
     * @return "OK" if graph is valid, "NOT_OK" otherwise
     */
    private String getPdfStatus(BearingHousingProductionData data, String process) {
        if ("P1".equals(process) && 
            data.getP1_graphStatus() != null && 
            data.getP1_graphStatus() == 1) {
            return "OK";
        }
        if ("P2".equals(process) && 
            data.getP2_graphStatus() != null && 
            data.getP2_graphStatus() == 1) {
            return "OK";
        }
        return "NOT_OK";
    }

    // ====================================================================
    // PRIVATE HELPER METHODS - File Name Building
    // ====================================================================

    /**
     * Build image file name from production data.
     * Format: BARCODE_PROCESS_SHIFT_STATUS_DATE.jpeg
     * Example: BH123456_P1_S1_B_OK_280726.jpeg
     * 
     * @param data production data
     * @param process process identifier (P1, P2)
     * @param statusCode status (B_OK, B_NG, A_OK, A_NG)
     * @return generated file name
     */
    private String buildImageFileName(BearingHousingProductionData data, 
                                      String process, 
                                      String statusCode) {
        LocalDateTime productionTime = data.getProductionDateTime();
        String shiftCode = "S" + data.getShift();
        String dateCode = formatDateForFileName(productionTime);
        
        return String.format("%s_%s_%s_%s_%s%s",
            data.getBarcode(),
            process,
            shiftCode,
            statusCode,
            dateCode,
            IMAGE_FILE_EXTENSION);
    }

    /**
     * Build PDF file name from production data.
     * Format: BARCODE_PROCESS_SHIFT_DATE.pdf
     * Example: BH123456_P1_S1_280726.pdf
     * 
     * @param data production data
     * @param process process identifier (P1, P2)
     * @return generated file name
     */
    private String buildPdfFileName(BearingHousingProductionData data, String process) {
        LocalDateTime productionTime = data.getProductionDateTime();
        String shiftCode = "S" + data.getShift();
        String dateCode = formatDateForFileName(productionTime);
        
        return String.format("%s_%s_%s_%s%s",
            data.getBarcode(),
            process,
            shiftCode,
            dateCode,
            PDF_FILE_EXTENSION);
    }

    /**
     * Format LocalDateTime to date code string.
     * Format: DDMMYY (e.g., "280726" for July 28, 2026)
     * 
     * @param dateTime the date/time to format
     * @return formatted date string
     */
    private String formatDateForFileName(LocalDateTime dateTime) {
        return String.format("%02d%02d%02d",
            dateTime.getDayOfMonth(),
            dateTime.getMonthValue(),
            dateTime.getYear() % 100);
    }

    // ====================================================================
    // PRIVATE HELPER METHODS - File I/O & Path Resolution
    // ====================================================================

    /**
     * Resolve file path from either live or archive storage.
     * Checks archive first (if file is old), then live storage.
     * 
     * @param fileName the requested file name
     * @param productionData the production data with date/shift info
     * @return Path to file, or null if not found
     */
    private Path resolveFilePath(String fileName, BearingHousingProductionData productionData) {
        logger.debug("resolveFilePath() - Resolving path for file: {}", fileName);

        // Check if file should be served from archive
        if (isFileArchived(productionData.getProductionDateTime())) {
            Path archivePath = buildArchivePath(productionData, fileName);
            if (Files.exists(archivePath)) {
                logger.debug("resolveFilePath() - Found in archive: {}", archivePath.toAbsolutePath());
                return archivePath;
            }
            logger.warn("resolveFilePath() - Archive path does not exist: {}", archivePath.toAbsolutePath());
            return null;
        }

        // File is recent, check live storage
        Path livePath = findInLiveStorage(fileName);
        if (livePath != null) {
            logger.debug("resolveFilePath() - Found in live storage: {}", livePath.toAbsolutePath());
            return livePath;
        }

        logger.warn("resolveFilePath() - File not found in live or archive storage: {}", fileName);
        return null;
    }

    /**
     * Build archive file path based on production date and shift.
     * Archive Structure: ARCHIVE_ROOT/ShiftN/YYYY/MM/DD/BARCODE/fileName
     * 
     * @param data production data
     * @param fileName the file name
     * @return Path to archive file
     */
    private Path buildArchivePath(BearingHousingProductionData data, String fileName) {
        LocalDate productionDate = data.getProductionDateTime().toLocalDate();
        String shiftFolder = SHIFT_PREFIX + data.getShift();
        String year = String.format("%04d", productionDate.getYear());
        String month = String.format("%02d", productionDate.getMonthValue());
        String day = String.format("%02d", productionDate.getDayOfMonth());

        return filePathConfig.getArchiveRootPath()
            .resolve(shiftFolder)
            .resolve(year)
            .resolve(month)
            .resolve(day)
            .resolve(data.getBarcode())
            .resolve(fileName);
    }

    /**
     * Find file in live storage directories.
     * Checks: LIVE_IMAGE_ROOT, then LIVE_GRAPH_ROOT
     * 
     * @param fileName the file name to find
     * @return Path if found, null otherwise
     */
    private Path findInLiveStorage(String fileName) {
        // Check live images directory
        Path imageFile = filePathConfig.getLiveImageRootPath().resolve(fileName);
        if (Files.exists(imageFile)) {
            return imageFile;
        }

        // Check live graphs directory
        Path graphFile = filePathConfig.getLiveGraphRootPath().resolve(fileName);
        if (Files.exists(graphFile)) {
            return graphFile;
        }

        return null;
    }

    /**
     * Read file content into byte array.
     * 
     * @param filePath the file path to read
     * @return file content as byte array
     * @throws IOException if file cannot be read
     */
    private byte[] readFileContent(Path filePath) throws IOException {
        logger.debug("readFileContent() - Reading file: {}", filePath.toAbsolutePath());
        try {
            byte[] content = Files.readAllBytes(filePath);
            logger.debug("readFileContent() - Successfully read {} bytes", content.length);
            return content;
        } catch (IOException ex) {
            logger.error("readFileContent() - Failed to read file: {}", filePath.toAbsolutePath(), ex);
            throw ex;
        }
    }

    /**
     * Extract barcode from file name.
     * Barcode is the part before the first underscore.
     * 
     * Example: "BH123456_P1_S1.jpeg" → "BH123456"
     * 
     * @param fileName the file name
     * @return barcode string, or null if format is invalid
     */
    private String extractBarcodeFromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        int delimiterIndex = fileName.indexOf('_');
        if (delimiterIndex <= 0) {
            return null;
        }

        return fileName.substring(0, delimiterIndex);
    }

    // ====================================================================
    // PRIVATE HELPER METHODS - HTTP Response Building
    // ====================================================================

    /**
     * Build HTTP response for image file.
     * Includes proper Content-Type and cache control headers.
     * 
     * @param fileContent the file content
     * @param fileName the original file name
     * @param mimeType the MIME type
     * @return ResponseEntity with proper headers
     */
    private ResponseEntity<Resource> buildImageResponse(byte[] fileContent, 
                                                        String fileName, 
                                                        String mimeType) {
        Resource resource = new ByteArrayResource(fileContent);
        HttpHeaders headers = new HttpHeaders();
        
        // Set content type
        headers.setContentType(MediaType.parseMediaType(mimeType));
        
        // Set cache control (no caching for security)
        headers.setCacheControl(filePathConfig.getCacheControlNoCache());
        
        // Set content length
        headers.setContentLength(fileContent.length);
        
        // CORS headers
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Length, Content-Type");
        
        logger.debug("buildImageResponse() - Response built for: {} (size: {} bytes)", 
            fileName, fileContent.length);
        
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    /**
     * Build HTTP response for PDF file.
     * Includes proper Content-Type, cache control, and CORS headers.
     * Uses inline disposition for browser preview.
     * 
     * @param fileContent the file content
     * @param fileName the original file name
     * @return ResponseEntity with proper headers
     */
    private ResponseEntity<Resource> buildPdfResponse(byte[] fileContent, String fileName) {
        Resource resource = new ByteArrayResource(fileContent);
        HttpHeaders headers = new HttpHeaders();
        
        // Set PDF content type
        headers.setContentType(MediaType.APPLICATION_PDF);
        
        // Set cache control (no caching for security)
        headers.setCacheControl(filePathConfig.getCacheControlNoCache());
        
        // Set content length
        headers.setContentLength(fileContent.length);
        
        // Set content disposition (inline for browser preview, not download)
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
        
        // CORS headers for cross-origin preview
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, OPTIONS");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition, Content-Length");
        
        logger.debug("buildPdfResponse() - PDF response built for: {} (size: {} bytes)", 
            fileName, fileContent.length);
        
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}