 package TVS_SFL.BearingHousingBackend.repositories;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

// import org.springframework.data.jpa.repository.JpaRepository;

// import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;

// public interface BearingHousingProductionDataRepository extends JpaRepository<BearingHousingProductionData, Long> {

// }

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BearingHousingProductionDataRepository {

    private static final String QUERY_OLDER_THAN_DATE =
            "SELECT * FROM bearing_housing_production_data WHERE production_date_time < ?";

    private final JdbcTemplate jdbcTemplate;

    public List<BearingHousingProductionData> findProductionDataOlderThan(LocalDate cutoffDate) {
        Timestamp cutoffTimestamp = Timestamp.valueOf(cutoffDate.atStartOfDay());
        return jdbcTemplate.query(
                QUERY_OLDER_THAN_DATE,
                (rs, rowNum) -> {
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
                    data.setP1_toxStartLoad(rs.getFloat("p1_tox_start_load"));
                    data.setP1_toxMidLoad(rs.getFloat("p1_tox_mid_load"));
                    data.setP1_toxEndLoad(rs.getFloat("p1_tox_end_load"));
                    data.setP1_toxStartDisplacement(rs.getFloat("p1_tox_start_displacement"));
                    data.setP1_toxMidDisplacement(rs.getFloat("p1_tox_mid_displacement"));
                    data.setP1_toxEndDisplacement(rs.getFloat("p1_tox_end_displacement"));
                    data.setP1_graphStatus(rs.getInt("p1_graph_status"));
                    data.setP2_beforeGlueStatus(rs.getInt("p2_before_glue_status"));
                    data.setP2_afterGlueStatus(rs.getInt("p2_after_glue_status"));
                    data.setP2_toxStartLoad(rs.getFloat("p2_tox_start_load"));
                    data.setP2_toxMidLoad(rs.getFloat("p2_tox_mid_load"));
                    data.setP2_toxEndLoad(rs.getFloat("p2_tox_end_load"));
                    data.setP2_toxStartDisplacement(rs.getFloat("p2_tox_start_displacement"));
                    data.setP2_toxMidDisplacement(rs.getFloat("p2_tox_mid_displacement"));
                    data.setP2_toxEndDisplacement(rs.getFloat("p2_tox_end_displacement"));
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
                },
                cutoffTimestamp
        );
    }

    public BearingHousingProductionData findByBarcode(String barcode) {
        String sql = """
            SELECT *
            FROM bearing_housing_production_data
            WHERE barcode = ?
        """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    BearingHousingProductionData data =
                            new BearingHousingProductionData();

                    data.setId(rs.getLong("id"));

                    data.setBarcode(
                            rs.getString("barcode"));

                    data.setSku(
                            rs.getString("sku"));

                    data.setNumberofProcess(
                            rs.getInt("number_of_process"));        

                    data.setShift(
                            rs.getInt("shift"));

                    data.setOperatorName(
                            rs.getString("operator_name"));

                    data.setCycleTime(
                            rs.getInt("cycle_time"));

                    data.setP1_beforeGlueStatus(
                            rs.getInt("p1_before_glue_status"));

                    data.setP1_afterGlueStatus(
                            rs.getInt("p1_after_glue_status"));
                    

                    data.setP1_toxStartLoad(
                            rs.getFloat("p1_tox_start_load"));

                    data.setP1_toxMidLoad(
                            rs.getFloat("p1_tox_mid_load"));

                    data.setP1_toxEndLoad(
                            rs.getFloat("p1_tox_end_load"));
                            

                    data.setP1_toxStartDisplacement(
                            rs.getFloat("p1_tox_start_displacement"));

                    data.setP1_toxMidDisplacement(
                            rs.getFloat("p1_tox_mid_displacement"));

                    data.setP1_toxEndDisplacement(
                            rs.getFloat("p1_tox_end_displacement"));
                     
                    data.setP1_graphStatus(
                            rs.getInt("p1_graph_status"));     

                    data.setP2_beforeGlueStatus(
                            rs.getInt("p2_before_glue_status"));

                    data.setP2_afterGlueStatus(
                            rs.getInt("p2_after_glue_status"));                    

                    data.setP2_toxStartLoad(
                            rs.getFloat("p2_tox_start_load"));

                    data.setP2_toxMidLoad(
                            rs.getFloat("p2_tox_mid_load"));

                    data.setP2_toxEndLoad(
                            rs.getFloat("p2_tox_end_load"));

                    data.setP2_toxStartDisplacement(
                            rs.getFloat("p2_tox_start_displacement"));

                    data.setP2_toxMidDisplacement(
                            rs.getFloat("p2_tox_mid_displacement"));

                    data.setP2_toxEndDisplacement(
                            rs.getFloat("p2_tox_end_displacement"));

                    data.setP2_graphStatus(
                            rs.getInt("p2_graph_status"));        
                    data.setCupConsumed(
                            rs.getInt("cup_consumed"));
                            
                    data.setFinalStatus(
                            rs.getInt("final_status"));        

                    Timestamp productionTs =
                            rs.getTimestamp(
                                    "production_date_time");

                    if (productionTs != null) {
                        data.setProductionDateTime(
                                productionTs.toLocalDateTime());
                    }

                    return data;
                },
                barcode);
    }
}
