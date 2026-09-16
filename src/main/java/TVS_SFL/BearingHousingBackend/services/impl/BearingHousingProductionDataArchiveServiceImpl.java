package TVS_SFL.BearingHousingBackend.services.impl;

import java.sql.Timestamp;
import java.sql.Time;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TVS_SFL.BearingHousingBackend.constants.SqlQueries;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.repositories.BearingHousingProductionDataRepository;
import TVS_SFL.BearingHousingBackend.services.BearingHousingProductionDataArchiveService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BearingHousingProductionDataArchiveServiceImpl implements BearingHousingProductionDataArchiveService {

    private final JdbcTemplate jdbcTemplate;
    private final BearingHousingProductionDataRepository productionDataRepository;

    @Override
    @Transactional
    public int archiveRecordsOlderThanYears(int years) {
        List<BearingHousingProductionData> records = productionDataRepository.findProductionDataOlderThan(
                java.time.LocalDate.now().minusYears(years)
        );

        if (records == null || records.isEmpty()) {
            return 0;
        }

        int archivedCount = 0;
        for (BearingHousingProductionData record : records) {
            int inserted = jdbcTemplate.update(
                    SqlQueries.INSERT_INTO_ARCHIVE,
                    record.getId(),
                    record.getBarcode(),
                    record.getOperatorName(),
                    record.getShift(),
                    record.getSku(),
                    record.getNumberofProcess(),
                    record.getCycleStartTime() != null
                    ? Time.valueOf(record.getCycleStartTime())
                            : null,
                    record.getP1_beforeGlueStatus(),
                    record.getP1_afterGlueStatus(),
                    record.getP1_toxStartLoad(),
                    record.getP1_toxMidLoad(),
                    record.getP1_toxEndLoad(),
                    record.getP1_toxStartDisplacement(),
                    record.getP1_toxMidDisplacement(),
                    record.getP1_toxEndDisplacement(),
                    record.getP1_graphStatus(),
                    record.getP2_beforeGlueStatus(),
                    record.getP2_afterGlueStatus(),
                    record.getP2_toxStartLoad(),
                    record.getP2_toxMidLoad(),
                    record.getP2_toxEndLoad(),
                    record.getP2_toxStartDisplacement(),
                    record.getP2_toxMidDisplacement(),
                    record.getP2_toxEndDisplacement(),
                    record.getP2_graphStatus(),
                    record.getCupConsumed(),
                    record.getFinalStatus(),
                    record.getOkCount(),
                    record.getNotOkCount(),
                    record.getTotalPartCount(),
                    record.getCycleTime(),
                    record.getProductionDateTime() != null ? Timestamp.valueOf(record.getProductionDateTime()) : null
            );

            if (inserted > 0) {
                jdbcTemplate.update(
                        "DELETE FROM bearing_housing_production_data WHERE id = ?",
                        record.getId()
                );
                archivedCount++;
            }
        }

        return archivedCount;
    }
}
