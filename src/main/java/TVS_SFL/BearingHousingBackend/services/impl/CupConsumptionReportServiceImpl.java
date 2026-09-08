package TVS_SFL.BearingHousingBackend.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import TVS_SFL.BearingHousingBackend.dto.CupConsumptionReportRow;
import TVS_SFL.BearingHousingBackend.repositories.CupConsumptionReportRepository;
import TVS_SFL.BearingHousingBackend.services.CupConsumptionReportService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CupConsumptionReportServiceImpl implements CupConsumptionReportService {

    private final CupConsumptionReportRepository repository;

    @Override
    public List<CupConsumptionReportRow> getDayReport(String from, String to, Integer shift) {
        return repository.getDayReport(from, to, shift);
    }

    @Override
    public List<CupConsumptionReportRow> getMonthReport(int year, Integer month, Integer shift) {
        return repository.getMonthReport(year, month, shift);
    }

    @Override
    public List<CupConsumptionReportRow> getYearReport(int year, Integer shift) {
        return repository.getYearReport(year, shift);
    }
}
