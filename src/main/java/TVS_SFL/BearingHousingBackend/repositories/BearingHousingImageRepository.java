package TVS_SFL.BearingHousingBackend.repositories;

import java.sql.Timestamp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BearingHousingImageRepository {

    private final JdbcTemplate jdbcTemplate;

    public BearingHousingProductionData findByBarcode(String barcode) {

        String sql = """
                SELECT *
                FROM bearing_housing_production_data
                WHERE barcode = ?
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

          BearingHousingProductionData data = new BearingHousingProductionData();

          data.setId(rs.getLong("id"));
          data.setBarcode(rs.getString("barcode"));          
          data.setShift(rs.getInt("shift"));
          data.setSku(rs.getString("sku"));
          data.setNumberofProcess(rs.getInt("number_of_process"));          
          data.setOperatorName(rs.getString("operator_name"));
          data.setCycleStartTime(
                                  rs.getTimestamp("cycle_start_time") != null
                                ? rs.getTimestamp("cycle_start_time").toLocalDateTime()
                                : null);

        //   data.setCycleEndTime(
        //                     rs.getTimestamp("cycle_end_time") != null
        //                   ? rs.getTimestamp("cycle_end_time").toLocalDateTime()
        //                   : null);

          data.setCycleTime(rs.getFloat("cycle_time"));

          Timestamp productionTimestamp = rs.getTimestamp("production_date_time");

          if (productionTimestamp != null) {
              data.setProductionDateTime(productionTimestamp.toLocalDateTime());
          }

          data.setP1_beforeGlueStatus(rs.getInt("p1_before_glue_status"));

          data.setP1_afterGlueStatus(rs.getInt("p1_after_glue_status"));

          data.setP1_toxLoadMax(rs.getFloat("p1_tox_load_max"));

          data.setP1_toxLoadMin(rs.getFloat("p1_tox_load_min"));

          data.setP1_toxLoadActual(rs.getFloat("p1_tox_load_actual"));

          data.setP1_toxDisplacementMax(rs.getFloat("p1_tox_displacement_max"));

          data.setP1_toxDisplacementMin(rs.getFloat("p1_tox_displacement_min"));

          data.setP1_toxDisplacementActual(rs.getFloat("p1_tox_displacement_actual"));

          data.setP2_beforeGlueStatus(rs.getInt("p2_before_glue_status"));

          data.setP2_afterGlueStatus(rs.getInt("p2_after_glue_status"));

          data.setP2_toxLoadMax(rs.getFloat("p2_tox_load_max"));

          data.setP2_toxLoadMin(rs.getFloat("p2_tox_load_min"));

          data.setP2_toxLoadActual(rs.getFloat("p2_tox_load_actual"));

          data.setP2_toxDisplacementMax(rs.getFloat("p2_tox_displacement_max"));

          data.setP2_toxDisplacementMin(rs.getFloat("p2_tox_displacement_min"));

          data.setP2_toxDisplacementActual(rs.getFloat("p2_tox_displacement_actual"));

          data.setFinalStatus(rs.getInt("final_status"));

          data.setTotalPartCount(rs.getInt("total_part_count"));

          data.setOkCount(rs.getInt("ok_count"));

          data.setNotOkCount(rs.getInt("not_ok_count"));

          return data;
      },
        barcode);
    }
}