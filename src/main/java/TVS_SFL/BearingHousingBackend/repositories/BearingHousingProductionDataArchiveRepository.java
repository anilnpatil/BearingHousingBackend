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
            data.setNumberofProcess(rs.getObject("number_of_process", Integer.class));
            data.setShift(rs.getObject("shift", Integer.class));
            data.setOperatorName(rs.getString("operator_name"));
            Time cycleStartTime = rs.getTime("cycle_start_time");
            if (cycleStartTime != null) {
                data.setCycleStartTime(cycleStartTime.toLocalTime());
            }
            data.setCycleTime(rs.getObject("cycle_time", Integer.class));
            data.setP1_beforeGlueStatus(rs.getObject("p1_before_glue_status", Integer.class));
            data.setP1_afterGlueStatus(rs.getObject("p1_after_glue_status", Integer.class));
            data.setP1_toxStartLoad(rs.getObject("p1_tox_start_load", Float.class));
            data.setP1_toxMidLoad(rs.getObject("p1_tox_mid_load", Float.class));
            data.setP1_toxEndLoad(rs.getObject("p1_tox_end_load", Float.class));
            data.setP1_toxStartDisplacement(rs.getObject("p1_tox_start_displacement", Float.class));
            data.setP1_toxMidDisplacement(rs.getObject("p1_tox_mid_displacement", Float.class));
            data.setP1_toxEndDisplacement(rs.getObject("p1_tox_end_displacement", Float.class));
            data.setP1_graphStatus(rs.getObject("p1_graph_status", Integer.class));
            data.setP2_beforeGlueStatus(rs.getObject("p2_before_glue_status", Integer.class));
            data.setP2_afterGlueStatus(rs.getObject("p2_after_glue_status", Integer.class));
            data.setP2_toxStartLoad(rs.getObject("p2_tox_start_load", Float.class));
            data.setP2_toxMidLoad(rs.getObject("p2_tox_mid_load", Float.class));
            data.setP2_toxEndLoad(rs.getObject("p2_tox_end_load", Float.class));
            data.setP2_toxStartDisplacement(rs.getObject("p2_tox_start_displacement", Float.class));
            data.setP2_toxMidDisplacement(rs.getObject("p2_tox_mid_displacement", Float.class));
            data.setP2_toxEndDisplacement(rs.getObject("p2_tox_end_displacement", Float.class));
            data.setP2_graphStatus(rs.getObject("p2_graph_status", Integer.class));
            data.setCupConsumed(rs.getObject("cup_consumed", Integer.class));
            data.setFinalStatus(rs.getObject("final_status", Integer.class));
            data.setTotalPartCount(rs.getObject("total_part_count", Integer.class));
            data.setOkCount(rs.getObject("ok_count", Integer.class));
            data.setNotOkCount(rs.getObject("not_ok_count", Integer.class));

            Timestamp productionTs = rs.getTimestamp("production_date_time");
            if (productionTs != null) {
                data.setProductionDateTime(productionTs.toLocalDateTime());
            }

            return data;
        };
    }
}
