package TVS_SFL.BearingHousingBackend.repositories;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import TVS_SFL.BearingHousingBackend.constants.BearingHousingConstants;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BearingHousingProductionDataArchiveRepository {

    private final JdbcTemplate jdbcTemplate;

    public BearingHousingProductionData findByBarcode(String barcode) {
        String sql = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " WHERE barcode = ? LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper(), barcode);
        } catch (Exception ex) {
            return null;
        }
    }

    public List<BearingHousingProductionData> findBySku(String sku) {
        String sql = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " WHERE sku = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, rowMapper(), sku);
    }

    public List<BearingHousingProductionData> findByOperatorName(String operatorName) {
        String sql = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " WHERE operator_name = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, rowMapper(), operatorName);
    }

    public List<BearingHousingProductionData> findByShift(Integer shift) {
        String sql = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " WHERE shift = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, rowMapper(), shift);
    }

    public BearingHousingProductionData findLatestByShift(Integer shift) {
        String sql = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " WHERE shift = ? ORDER BY id DESC LIMIT 1";
        List<BearingHousingProductionData> results = jdbcTemplate.query(sql, rowMapper(), shift);
        return results.isEmpty() ? null : results.get(0);
    }

    public BearingHousingProductionData findLatest() {
        String sql = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " ORDER BY id DESC LIMIT 1";
        List<BearingHousingProductionData> results = jdbcTemplate.query(sql, rowMapper());
        return results.isEmpty() ? null : results.get(0);
    }

    public List<BearingHousingProductionData> findByFinalStatus(Integer status) {
        String sql = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " WHERE final_status = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, rowMapper(), status);
    }

    private org.springframework.jdbc.core.RowMapper<BearingHousingProductionData> rowMapper() {
        return (rs, rowNum) -> {
            BearingHousingProductionData data = new BearingHousingProductionData();
            data.setId(rs.getLong("id"));
            data.setBarcode(rs.getString("barcode"));
            data.setSku(rs.getString("sku"));
            data.setNumberofProcess(rs.getInt("number_of_process"));
            data.setShift(rs.getInt("shift"));
            data.setOperatorName(rs.getString("operator_name"));
            Time cycleStartTime = rs.getTime("cycle_start_time");
            if (cycleStartTime != null) {
                data.setCycleStartTime(cycleStartTime.toLocalTime());
            }
            data.setCycleTime(rs.getInt("cycle_time"));
            data.setP1_beforeGlueStatus(rs.getInt("p1_before_glue_status"));
            data.setP1_afterGlueStatus(rs.getInt("p1_after_glue_status"));
            data.setP1_toxLoadActual(rs.getFloat("p1_tox_load_actual"));
            data.setP1_toxDisplacementMax(rs.getFloat("p1_tox_displacement_max"));
            data.setP1_toxDisplacementMin(rs.getFloat("p1_tox_displacement_min"));
            data.setP1_toxDisplacementActual(rs.getFloat("p1_tox_displacement_actual"));
            data.setP1_graphStatus(rs.getInt("p1_graph_status"));
            data.setP2_beforeGlueStatus(rs.getInt("p2_before_glue_status"));
            data.setP2_afterGlueStatus(rs.getInt("p2_after_glue_status"));
            data.setP2_toxLoadActual(rs.getFloat("p2_tox_load_actual"));
            data.setP2_toxDisplacementMax(rs.getFloat("p2_tox_displacement_max"));
            data.setP2_toxDisplacementMin(rs.getFloat("p2_tox_displacement_min"));
            data.setP2_toxDisplacementActual(rs.getFloat("p2_tox_displacement_actual"));
            data.setP2_graphStatus(rs.getInt("p2_graph_status"));
            data.setCupConsumed(rs.getInt("cup_consumed"));
            data.setFinalStatus(rs.getInt("final_status"));
            data.setTotalPartCount(rs.getInt("total_part_count"));
            data.setOkCount(rs.getInt("ok_count"));
            data.setNotOkCount(rs.getInt("not_ok_count"));

            Timestamp productionTs = rs.getTimestamp("production_date_time");
            if (productionTs != null) {
                data.setProductionDateTime(productionTs.toLocalDateTime());
            }

            return data;
        };
    }
}
