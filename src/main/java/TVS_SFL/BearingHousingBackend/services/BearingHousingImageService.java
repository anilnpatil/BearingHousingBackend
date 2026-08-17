package TVS_SFL.BearingHousingBackend.services;

import java.io.IOException;

import TVS_SFL.BearingHousingBackend.dto.BearingHousingDataToImageResponse;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/**
 * ========================================================================
 * BEARING HOUSING IMAGE SERVICE - INTERFACE
 * ========================================================================
 * Purpose: Defines the contract for bearing housing image and data operations.
 *          Handles retrieval of production data with associated images and PDFs.
 * 
 * Responsibilities:
 * - Retrieve production data by barcode
 * - Build image URLs and metadata
 * - Build PDF URLs and metadata
 * - Serve image files with proper content types
 * - Serve PDF files with proper headers
 * 
 * Implementation: BearingHousingImageServiceImpl
 * Used By: BearingHousingImageController
 * Dependencies: BearingHousingImageRepository
 * 
 * Thread-Safe: Implementation should be thread-safe
 * Cache-Friendly: URLs can be cached by HTTP clients
 * ========================================================================
 */
public interface BearingHousingImageService {

    /**
     * Retrieve complete image and PDF data for a production record.
     * 
     * Functionality:
     * - Queries database for production data by barcode
     * - Builds file URLs for all images and PDFs
     * - Organizes data by process (P1, P2) and type (before, after, graphs)
     * - Validates production data existence
     * 
     * Performance:
     * - Single database query
     * - String operations for URL building (O(1))
     * - Overall complexity: O(1)
     * 
     * @param barcode the unique bearing housing barcode (e.g., "BH123456")
     * @return BearingHousingDataToImageResponse containing production data
     *         and image/PDF URLs organized by process
     * @throws RuntimeException if barcode not found in database
     * 
     * Example Response:
     * {
     *   "productionData": { ... },
     *   "p1_beforeImage": { "status": "B_OK", "url": "/api/bearing-housing/image/..." },
     *   "p1_afterImage": { "status": "A_OK", "url": "/api/bearing-housing/image/..." },
     *   "p1_graphImage": { "status": "OK", "url": "/api/bearing-housing/pdf/..." },
     *   "p2_beforeImage": { ... }, // if number_of_process >= 2
     *   ...
     * }
     */
    BearingHousingDataToImageResponse getData(String barcode);

    /**
     * Serve an image file with proper HTTP headers.
     * 
     * Functionality:
     * - Validates file extension against whitelist
     * - Resolves file path (live or archived)
     * - Reads file content from disk
     * - Sets appropriate Content-Type header
     * - Prevents caching for security
     * 
     * Performance:
     * - Database query for barcode metadata (O(1))
     * - File I/O (depends on file size)
     * - Network I/O (depends on file size)
     * 
     * Security:
     * - Validates file extension (whitelist approach)
     * - Prevents directory traversal via barcode validation
     * - Sets no-cache headers to prevent sensitive data caching
     * 
     * @param fileName the requested image file name (with extension)
     * @return ResponseEntity with image resource and proper headers
     * @throws IOException if file cannot be read
     * @throws RuntimeException if barcode validation fails
     * 
     * Supported Formats: .jpeg, .jpg, .png, .gif, .bmp, .webp
     */
    ResponseEntity<Resource> getImageFile(String fileName) throws IOException;

    /**
     * Serve a PDF file with proper HTTP headers.
     * 
     * Functionality:
     * - Validates file extension is .pdf
     * - Resolves file path (live or archived)
     * - Reads file content from disk
     * - Sets Content-Disposition header (inline for preview)
     * - Adds CORS headers for cross-origin access
     * 
     * Performance:
     * - Database query for barcode metadata (O(1))
     * - File I/O (depends on file size)
     * - Network I/O (depends on file size)
     * 
     * Security:
     * - Strict .pdf extension validation
     * - Prevents directory traversal
     * - CORS properly configured for client-side preview
     * 
     * @param fileName the requested PDF file name (must end with .pdf)
     * @return ResponseEntity with PDF resource and proper headers
     * @throws IOException if file cannot be read
     * @throws RuntimeException if validation fails
     * 
     * Supported Format: .pdf only
     * HTTP Header "Content-Disposition": inline (for browser preview)
     */
    ResponseEntity<Resource> getPdfFile(String fileName) throws IOException;

    /**
     * Determine if a file path should be served from archive or live storage.
     * 
     * Functionality:
     * - Checks production date against archive threshold
     * - Files older than 90 days → archive storage
     * - Files newer than 90 days → live storage
     * 
     * Performance:
     * - LocalDateTime comparison: O(1)
     * 
     * @param productionDateTime the production date/time to check
     * @return true if file is archived (older than 90 days)
     */
    boolean isFileArchived(java.time.LocalDateTime productionDateTime);

    /**
     * Validate barcode format to prevent directory traversal attacks.
     * 
     * Functionality:
     * - Extracts barcode from filename (part before first underscore)
     * - Validates format: non-empty, no path traversal characters
     * - Checks against database for existence
     * 
     * Performance:
     * - String operations: O(1)
     * - Database query: O(1) with indexed barcode column
     * 
     * @param fileName the file name from which to extract barcode
     * @return ProductionData if barcode is valid and exists
     * @throws RuntimeException if validation fails
     */
    BearingHousingProductionData validateAndGetBarcode(String fileName);
}