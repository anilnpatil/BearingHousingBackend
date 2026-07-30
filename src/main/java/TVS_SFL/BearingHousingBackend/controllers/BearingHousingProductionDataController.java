package TVS_SFL.BearingHousingBackend.controllers;

import TVS_SFL.BearingHousingBackend.constants.BearingHousingConstants;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.exceptions.BadRequestException;
import TVS_SFL.BearingHousingBackend.exceptions.ResourceNotFoundException;
import TVS_SFL.BearingHousingBackend.services.BearingHousingProductionDataService;
import TVS_SFL.BearingHousingBackend.dto.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(BearingHousingConstants.PARAMETER_API_BASE_PATH)
@CrossOrigin(origins = "*", maxAge = 3600)
public class BearingHousingProductionDataController {

    @Autowired
    private BearingHousingProductionDataService productionDataService;

   // Photo storage configuration
    @Value("${bearinghousing.photos.directory:photos}")
    private String photoStorageDirectory;
    @Value("${bearinghousing.photos.directories:}")
    private String photoStorageDirectories;

    /**     * Get all production data     */
    @GetMapping(BearingHousingConstants.GET_ALL_ENDPOINT)
    public ResponseEntity<List<BearingHousingProductionData>> getAllProductionData() {
        List<BearingHousingProductionData> data = productionDataService.getAllProductionData();
        return ResponseEntity.ok(data);
    }

    /**     * Get production data by ID     */
    @GetMapping(BearingHousingConstants.GET_BY_BARCODE_ENDPOINT)
    public ResponseEntity<BearingHousingProductionData> getProductionDataByBarcode(@PathVariable String barcode) {
        BearingHousingProductionData data = productionDataService.getProductionDataByBarcode(barcode);
        if (data != null) {
            return ResponseEntity.ok(data);
        }
        throw new ResourceNotFoundException("Production data not found for barcode: " + barcode);
    }

    /**     * Create new production data    */
    @PostMapping(BearingHousingConstants.CREATE_ENDPOINT)
    public ResponseEntity<BearingHousingProductionData> createProductionData(@RequestBody BearingHousingProductionData dto) {
        BearingHousingProductionData savedData = productionDataService.saveProductionData(dto);
        if (savedData != null) {
            return ResponseEntity.status(201).body(savedData);
        }
        throw new BadRequestException("Failed to create production data. Please verify the request body.");
    }

    /**     * Update production data     */
    @PutMapping(BearingHousingConstants.UPDATE_ENDPOINT)
    public ResponseEntity<BearingHousingProductionData> updateProductionData( @PathVariable String barcode, @RequestBody BearingHousingProductionData dto) {
        BearingHousingProductionData updatedData = productionDataService.updateProductionData(barcode, dto);
        if (updatedData != null) {
            return ResponseEntity.ok(updatedData);
        }
        throw new ResourceNotFoundException("Unable to update production data; record not found for id: " + barcode);
    }

    /**     * Delete production data      */
    @DeleteMapping(BearingHousingConstants.DELETE_ENDPOINT)
    public ResponseEntity<Void> deleteProductionData(@PathVariable String barcode) {
        BearingHousingProductionData existingData = productionDataService.getProductionDataByBarcode(barcode);
        if (existingData != null) {
            productionDataService.deleteProductionData(barcode);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Production data not found for deletion with barcode: " + barcode);
    }

    /**     * Get production data by SKU     */
    @GetMapping(BearingHousingConstants.GET_BY_SKU_ENDPOINT)
    public ResponseEntity<List<BearingHousingProductionData>> getProductionDataBySku(@PathVariable String sku) {
        List<BearingHousingProductionData> data = productionDataService.getProductionDataBySku(sku);
        return ResponseEntity.ok(data);
    }

    /**     * Get production data by operator name     */
    @GetMapping(BearingHousingConstants.GET_BY_OPERATOR_ENDPOINT)
    public ResponseEntity<List<BearingHousingProductionData>> getProductionDataByOperator(
            @PathVariable String operatorName) {
        List<BearingHousingProductionData> data = productionDataService.getProductionDataByOperatorName(operatorName);
        return ResponseEntity.ok(data);
    }

    /**     * Get latest production data by shift     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(BearingHousingConstants.GET_BY_SHIFT_ENDPOINT)
    public ResponseEntity<BearingHousingProductionData> getProductionDataByShift(
            @RequestParam Integer shift) {
        BearingHousingProductionData data = productionDataService.getLatestProductionDataByShift(shift);
        if (data != null) {
            return ResponseEntity.ok(data);
        }
        throw new ResourceNotFoundException("Production data not found for shift: " + shift);
    }

    /**     * Get production data by final status     */
    @GetMapping(BearingHousingConstants.GET_BY_STATUS_ENDPOINT)
    public ResponseEntity<List<BearingHousingProductionData>> getProductionDataByStatus(
            @PathVariable Integer status) {
        List<BearingHousingProductionData> data = productionDataService.getProductionDataByFinalStatus(status);
        return ResponseEntity.ok(data);
    }

    /**
     * Get production data by date range with optional shift and SKU filters and pagination
     * Query params: start (required), end (required), page (default: 0), size (default: 15), shift (optional), sku (optional)
     */
    @GetMapping(BearingHousingConstants.GET_BY_DATE_RANGE_ENDPOINT)
    public ResponseEntity<PaginatedResponse<BearingHousingProductionData>> getProductionDataByDateRange(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Integer shift,
            @RequestParam(required = false) String sku) {

        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            int pageNumber = parsePageOrDefault(page, 0);
            int pageSize = parsePageOrDefault(size, 15);

            if (startDate.isAfter(endDate)) {
                throw new BadRequestException("Start date must be before or equal to end date");
            }

            if (pageNumber < 0 || pageSize <= 0) {
                throw new BadRequestException("Page must be >= 0 and size must be > 0");
            }

            PaginatedResponse<BearingHousingProductionData> response = 
                    productionDataService.getProductionDataByDateRange(startDate, endDate, shift, sku, pageNumber, pageSize);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid date format. Please use YYYY-MM-DD format");
        }
    }

    private int parsePageOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank() || "undefined".equalsIgnoreCase(value) || "null".equalsIgnoreCase(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid pagination value. 'page' and 'size' must be integers.");
        }
    }
    
}

