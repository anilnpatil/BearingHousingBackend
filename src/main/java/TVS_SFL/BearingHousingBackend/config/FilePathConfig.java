package TVS_SFL.BearingHousingBackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ========================================================================
 * FILE PATH CONFIGURATION - CENTRALIZED PATH MANAGEMENT
 * ========================================================================
 * Purpose: Centralized configuration for all file paths used throughout
 *          the application. This ensures consistency and makes it easy to
 *          update paths without modifying multiple files.
 * ========================================================================
 */
@Component
public class FilePathConfig {
    
    // APPLICATION PROPERTIES (from application.yaml)       
    /**
     * Primary photo storage directory.
     * Default: photos
     * Environment Variable: PHOTO_STORAGE_DIR
     */
    @Value("${bearinghousing.photos.directory:photos}")
    private String photoStorageDirectory;

    /**
     * Additional photo storage directories (comma-separated).
     * Default: empty string
     * Environment Variable: PHOTO_STORAGE_DIRS
     */
    @Value("${bearinghousing.photos.directories:}")
    private String photoStorageDirectories;

    // ====================================================================
    // FILE SYSTEM PATHS - HARDCODED DEFAULTS FOR DEVELOPMENT
    // ====================================================================
    // NOTE: These paths should ideally be moved to application.yaml
    // for production deployments. Hardcoded values are for local development.
    
    /** Archive root directory for files older than 90 days */
    private static final String ARCHIVE_ROOT = "C:/BearingHousingOldFiles";
    
    /** Live images storage directory (Station IV-4) */
    private static final String LIVE_IMAGE_ROOT = "C:/BearingHousingImages/IV-4";
    
    /** Live graphs/reports storage directory */
    private static final String LIVE_GRAPH_ROOT = "C:/BearingHousingGraphs";

    // ====================================================================
    // ARCHIVE CONFIGURATION
    // ====================================================================
    
    /** Files older than this threshold (days) are moved to archive */
    private static final int ARCHIVE_THRESHOLD_DAYS = 90;
    
    /** Directory prefix for shift-based organization in archive */
    private static final String SHIFT_DIRECTORY_PREFIX = "Shift";

    // ====================================================================
    // FILE TYPE CONFIGURATION
    // ====================================================================
    
    /** Supported image file extensions (case-insensitive) */
    private static final String[] SUPPORTED_IMAGE_EXTENSIONS = {
        ".jpeg", ".jpg", ".png", ".gif", ".bmp", ".webp"
    };
    
    /** Supported document file extensions (case-insensitive) */
    private static final String[] SUPPORTED_DOCUMENT_EXTENSIONS = {
        ".pdf", ".doc", ".docx", ".xls", ".xlsx"
    };

    
    // CACHE CONTROL CONFIGURATION    
    
    /** Cache control header for static files */
    private static final String CACHE_CONTROL_NO_CACHE = 
        "no-cache, no-store, must-revalidate";
    
    /** Cache control header for occasionally-accessed files */
    private static final String CACHE_CONTROL_SHORT_TERM = 
        "public, max-age=3600";

    
    // PUBLIC ACCESSOR METHODS - Path Resolution 

    /** Get the archive root path object.
     * @return Path object pointing to archive directory
     */
    public Path getArchiveRootPath() {
        return Paths.get(ARCHIVE_ROOT);
    }

    /** Get the live images root path object.
     * @return Path object pointing to live images directory
     */
    public Path getLiveImageRootPath() {
        return Paths.get(LIVE_IMAGE_ROOT);
    }

    /** Get the live graphs root path object.
     * @return Path object pointing to live graphs directory
     */
    public Path getLiveGraphRootPath() {
        return Paths.get(LIVE_GRAPH_ROOT);
    }

    /** Get the photo storage directory from properties.
     * @return String path from application properties
     */
    public String getPhotoStorageDirectory() {
        return photoStorageDirectory;
    }

    /** Get additional photo storage directories.
     * @return Comma-separated string of directories
     */
    public String getPhotoStorageDirectories() {
        return photoStorageDirectories;
    }
    
    // PUBLIC ACCESSOR METHODS - Configuration Values
    

    /** Get the archive threshold in days.
     * Files older than this are considered archived.
     * @return number of days (default: 90)
     */
    public int getArchiveThresholdDays() {
        return ARCHIVE_THRESHOLD_DAYS;
    }

    /** Get the shift directory prefix used in archive structure.
     * @return prefix string (default: "Shift")
     */
    public String getShiftDirectoryPrefix() {
        return SHIFT_DIRECTORY_PREFIX;
    }

    /** Get supported image file extensions.
     * @return array of extensions (e.g., [".jpg", ".png", ".gif"])
     */
    public String[] getSupportedImageExtensions() {
        return SUPPORTED_IMAGE_EXTENSIONS.clone();
    }

    /**
     * Get supported document file extensions.
     * @return array of extensions (e.g., [".pdf", ".doc"])
     */
    public String[] getSupportedDocumentExtensions() {
        return SUPPORTED_DOCUMENT_EXTENSIONS.clone();
    }

    /** Get cache control header for static content.
     * @return cache control directive
     */
    public String getCacheControlNoCache() {
        return CACHE_CONTROL_NO_CACHE;
    }

    /** Get cache control header for occasionally-accessed content.
     * Get cache control header for occasionally-accessed content.
     * @return cache control directive with 1-hour max-age
     */
    public String getCacheControlShortTerm() {
        return CACHE_CONTROL_SHORT_TERM;
    }

    
    // UTILITY METHODS - File Extension Validation

    /* Check if a file extension is a supported image format.
     * Case-insensitive comparison.
     * 
     * @param fileName the file name to check
     * @return true if file is a supported image format
     */
    public boolean isImageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String lowerName = fileName.toLowerCase();
        for (String ext : SUPPORTED_IMAGE_EXTENSIONS) {
            if (lowerName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /** Check if a file extension is a supported document format.
     *  Case-insensitive comparison.
     * 
     * @param fileName the file name to check
     * @return true if file is a supported document format
     */
    public boolean isDocumentFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String lowerName = fileName.toLowerCase();
        for (String ext : SUPPORTED_DOCUMENT_EXTENSIONS) {
            if (lowerName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the MIME type for a given file.
     * Supports images and common document formats.
     * 
     * @param fileName the file name
     * @return MIME type string (e.g., "image/jpeg", "application/pdf")
     */
    public String getMimeType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String lowerName = fileName.toLowerCase();
        
        // Image MIME types
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) 
            return "image/jpeg";
        if (lowerName.endsWith(".png")) 
            return "image/png";
        if (lowerName.endsWith(".gif")) 
            return "image/gif";
        if (lowerName.endsWith(".bmp")) 
            return "image/bmp";
        if (lowerName.endsWith(".webp")) 
            return "image/webp";
        
        // Document MIME types
        if (lowerName.endsWith(".pdf")) 
            return "application/pdf";
        if (lowerName.endsWith(".doc")) 
            return "application/msword";
        if (lowerName.endsWith(".docx")) 
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lowerName.endsWith(".xls")) 
            return "application/vnd.ms-excel";
        if (lowerName.endsWith(".xlsx")) 
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        
        // Default
        return "application/octet-stream";
    } 

    // UTILITY METHODS - Path Building    
    /**
     * Extract the file extension from a filename (including the dot).     * 
     * @param fileName the file name
     * @return file extension (e.g., ".jpg") or empty string if none
     */
    public String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot);
        }
        return "";
    }

    /** Extract the file name without extension. 
     * @param fileName the full file name
     * @return file name without extension
     */
    public String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(0, lastDot);
        }
        return fileName;
    }
}
