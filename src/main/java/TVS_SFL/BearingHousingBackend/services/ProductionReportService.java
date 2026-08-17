package TVS_SFL.BearingHousingBackend.services;

import java.util.List;

import TVS_SFL.BearingHousingBackend.dto.ProductionReportRow;

public interface ProductionReportService {

    List<ProductionReportRow> getDayReport(
            String from,
            String to,
            String sku,
            Integer shift
    );

    List<ProductionReportRow> getWeekReport(
            int year,
            String sku,
            Integer shift
    );

    List<ProductionReportRow> getMonthReport(
            int year,
            String sku,
            Integer shift
    );
}