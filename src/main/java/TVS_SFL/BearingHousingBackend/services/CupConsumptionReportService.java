package TVS_SFL.BearingHousingBackend.services;

import java.util.List;

import TVS_SFL.BearingHousingBackend.dto.CupConsumptionReportRow;

public interface CupConsumptionReportService {

    List<CupConsumptionReportRow> getDayReport(
            String from,
            String to,
            Integer shift
    );

    List<CupConsumptionReportRow> getMonthReport(
            int year,
            Integer month,
            Integer shift
    );

    List<CupConsumptionReportRow> getYearReport(
            int year,
            Integer shift
    );
}
