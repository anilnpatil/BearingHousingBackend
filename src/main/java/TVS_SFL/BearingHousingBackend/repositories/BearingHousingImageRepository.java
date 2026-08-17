package TVS_SFL.BearingHousingBackend.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;

import lombok.RequiredArgsConstructor;

/**
 * Repository for BearingHousingProductionData database access.
 * Provides centralized row mapping and query execution.
 */
@Repository
@RequiredArgsConstructor
public class BearingHousingImageRepository {

    private static final Logger logger = LoggerFactory.getLogger(BearingHousingImageRepository.class);
    
    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY_BY_BARCODE = 
        "SELECT * FROM bearing_housing_production_data WHERE barcode = ?";
    
    private static final String QUERY_OLDER_THAN_DATE = 
        "SELECT * FROM bearing_housing_production_data WHERE production_date_time < ?";

    /**
     * Find production data by barcode. Returns null if not found.
     */
    public BearingHousingProductionData findByBarcode(String barcode) {
            try {
            return jdbcTemplate.queryForObject(
                QUERY_BY_BARCODE,
                getProductionDataRowMapper(),
                barcode
            );
        } catch (Exception ex) {
            logger.error("Error finding barcode: {}", barcode, ex);
            return null;
        }
    }

    /**
     * Find all production data older than specified date.
     */
    public List<BearingHousingProductionData> findProductionDataOlderThan(LocalDate cutoffDate) {
        Timestamp cutoffTimestamp = Timestamp.valueOf(cutoffDate.atStartOfDay());
        try {
            return jdbcTemplate.query(
                QUERY_OLDER_THAN_DATE,
                getProductionDataRowMapper(),
                cutoffTimestamp
            );
        } catch (Exception ex) {
            logger.error("Error finding records older than: {}", cutoffDate, ex);
            return List.of();
        }
    }

    private RowMapper<BearingHousingProductionData> getProductionDataRowMapper() {
        return (rs, rowNum) -> mapResultSetToProductionData(rs);
    }

    private BearingHousingProductionData mapResultSetToProductionData(ResultSet rs) throws SQLException {
        BearingHousingProductionData data = new BearingHousingProductionData();

        data.setId(rs.getLong("id"));
        data.setBarcode(rs.getString("barcode"));
        data.setShift(rs.getInt("shift"));
        data.setSku(rs.getString("sku"));
        data.setNumberofProcess(rs.getInt("number_of_process"));
        data.setOperatorName(rs.getString("operator_name"));

        Timestamp cycleStartTime = rs.getTimestamp("cycle_start_time");
        if (cycleStartTime != null) {
            data.setCycleStartTime(cycleStartTime.toLocalDateTime());
        }

        Timestamp productionTime = rs.getTimestamp("production_date_time");
        if (productionTime != null) {
            data.setProductionDateTime(productionTime.toLocalDateTime());
        }

        data.setCycleTime(rs.getFloat("cycle_time"));
        data.setP1_beforeGlueStatus(rs.getInt("p1_before_glue_status"));
        data.setP1_toxLoadMax(rs.getFloat("p1_tox_load_max"));
        data.setP1_toxLoadMin(rs.getFloat("p1_tox_load_min"));
        data.setP1_toxLoadActual(rs.getFloat("p1_tox_load_actual"));
        data.setP1_toxDisplacementMax(rs.getFloat("p1_tox_displacement_max"));
        data.setP1_toxDisplacementMin(rs.getFloat("p1_tox_displacement_min"));
        data.setP1_toxDisplacementActual(rs.getFloat("p1_tox_displacement_actual"));
        data.setP1_afterGlueStatus(rs.getInt("p1_after_glue_status"));
        data.setP1_graphStatus(rs.getInt("p1_graph_status"));

        data.setP2_beforeGlueStatus(rs.getInt("p2_before_glue_status"));
        data.setP2_toxLoadMax(rs.getFloat("p2_tox_load_max"));
        data.setP2_toxLoadMin(rs.getFloat("p2_tox_load_min"));
        data.setP2_toxLoadActual(rs.getFloat("p2_tox_load_actual"));
        data.setP2_toxDisplacementMax(rs.getFloat("p2_tox_displacement_max"));
        data.setP2_toxDisplacementMin(rs.getFloat("p2_tox_displacement_min"));
        data.setP2_toxDisplacementActual(rs.getFloat("p2_tox_displacement_actual"));
        data.setP2_afterGlueStatus(rs.getInt("p2_after_glue_status"));
        data.setP2_graphStatus(rs.getInt("p2_graph_status"));

        data.setFinalStatus(rs.getInt("final_status"));
        data.setTotalPartCount(rs.getInt("total_part_count"));
        data.setOkCount(rs.getInt("ok_count"));
        data.setNotOkCount(rs.getInt("not_ok_count"));

        return data;
    }
}
