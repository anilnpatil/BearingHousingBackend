package TVS_SFL.BearingHousingBackend.services;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.dto.PaginatedResponse;
import java.time.LocalDate;
import java.util.List;

public interface BearingHousingProductionDataService {
    
    List<BearingHousingProductionData> getAllProductionData();
    
    BearingHousingProductionData getProductionDataByBarcode(String barcode);
    BearingHousingProductionData getProductionDataAndPhotosByBarcode(String barcode);
    
    BearingHousingProductionData saveProductionData(BearingHousingProductionData dto);
    
    BearingHousingProductionData updateProductionData(String Barcode, BearingHousingProductionData dto);
    
    void deleteProductionData(String barcode);
    
    List<BearingHousingProductionData> getProductionDataBySku(String sku);
    
    List<BearingHousingProductionData> getProductionDataByOperatorName(String operatorName);

    List<String> getPhotoFileNamesByBarcode(String barcode);
    
    List<BearingHousingProductionData> getProductionDataByShift(Integer shift);

    BearingHousingProductionData getLatestProductionDataByShift(Integer shift);

    BearingHousingProductionData getLatestProductionData();
    
    List<BearingHousingProductionData> getProductionDataByFinalStatus(Integer status);

    PaginatedResponse<BearingHousingProductionData> getProductionDataByDateRange(
            LocalDate startDate, LocalDate endDate, Integer shift, String sku, int page, int size);
}
