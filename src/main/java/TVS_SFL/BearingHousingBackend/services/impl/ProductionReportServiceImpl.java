package TVS_SFL.BearingHousingBackend.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import TVS_SFL.BearingHousingBackend.dto.ProductionReportRow;
import TVS_SFL.BearingHousingBackend.repositories.ProductionReportRepository;
import TVS_SFL.BearingHousingBackend.services.ProductionReportService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionReportServiceImpl
        implements ProductionReportService {

    private final ProductionReportRepository productionReportRepository;

    @Override
    public List<ProductionReportRow> getDayReport(
            String from,
            String to,
            String sku,
            Integer shift) {

        return productionReportRepository.getDayReport(
                from,
                to,
                sku,
                shift
        );
    }

    @Override
    public List<ProductionReportRow> getWeekReport(
            int year,
            String sku,
            Integer shift) {

        return productionReportRepository.getWeekReport(
                year,
                sku,
                shift
        );
    }

    @Override
    public List<ProductionReportRow> getMonthReport(
            int year,
            String sku,
            Integer shift) {

        return productionReportRepository.getMonthReport(
                year,
                sku,
                shift
        );
    }
}