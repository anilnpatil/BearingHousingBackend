package TVS_SFL.BearingHousingBackend.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import TVS_SFL.BearingHousingBackend.dto.ProductionReportRow;

@Repository
@RequiredArgsConstructor
public class ProductionReportRepository {

    private final JdbcTemplate jdbcTemplate;

    
    // DAY REPORT    
    public List<ProductionReportRow> getDayReport(
            String from,
            String to,
            String sku,
            Integer shift) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                production_date_time::date AS report_date,
                sku,
                shift,
                MAX(total_part_count) AS total_count,
                MAX(ok_count) AS ok_count,
                MAX(not_ok_count) AS not_ok_count
            FROM bearing_housing_production_data
            WHERE production_date_time >= ?::timestamp
              AND production_date_time < ?::timestamp
            """);

        List<Object> params = new ArrayList<>();

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDateExclusive = LocalDate.parse(to).plusDays(1);

        params.add(fromDate.toString() + " 00:00:00");
        params.add(toDateExclusive.toString() + " 00:00:00");

        appendFilters(sql, params, sku, shift);

        sql.append("""
            GROUP BY
                production_date_time::date,
                sku,
                shift
            ORDER BY
                report_date,
                sku,
                shift
            """);

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                productionReportRowMapper()
        );
    }


    
    // WEEK REPORT    
    public List<ProductionReportRow> getWeekReport(
            int year,
            String sku,
            Integer shift) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                DATE_TRUNC('week', production_date_time)::date AS report_date,
                sku,
                shift,
                MAX(total_part_count) AS total_count,
                MAX(ok_count) AS ok_count,
                MAX(not_ok_count) AS not_ok_count
            FROM bearing_housing_production_data
            WHERE EXTRACT(YEAR FROM production_date_time) = ?
            """);

        List<Object> params = new ArrayList<>();
        params.add(year);

        appendFilters(sql, params, sku, shift);

        sql.append("""
            GROUP BY
                DATE_TRUNC('week', production_date_time)::date,
                sku,
                shift
            ORDER BY
                report_date,
                sku,
                shift
            """);

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                productionReportRowMapper()
        );
        
    }


    
    // MONTH REPORT    
    public List<ProductionReportRow> getMonthReport(
            int year,
            String sku,
            Integer shift) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                DATE_TRUNC('month', production_date_time)::date AS report_date,
                sku,
                shift,
                MAX(total_part_count) AS total_count,
                MAX(ok_count) AS ok_count,
                MAX(not_ok_count) AS not_ok_count
            FROM bearing_housing_production_data
            WHERE EXTRACT(YEAR FROM production_date_time) = ?
            """);

        List<Object> params = new ArrayList<>();
        params.add(year);

        appendFilters(sql, params, sku, shift);

        sql.append("""
            GROUP BY
                DATE_TRUNC('month', production_date_time)::date,
                sku,
                shift
            ORDER BY
                report_date,
                sku,
                shift
            """);

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                productionReportRowMapper()
        );
    }
    
    // COMMON FILTERS    
   private void appendFilters(
        StringBuilder sql,
        List<Object> params,
        String sku,
        Integer shift) {

    
    if (sku != null
            && !sku.isBlank()
            && !sku.equalsIgnoreCase("ALL")
            && !sku.equals("0")) {

        sql.append(" AND sku = ? ");
        params.add(sku);
    }
    
    if (shift != null && shift > 0) {
        sql.append(" AND shift = ? ");
        params.add(shift);
    }
}
   
    // ROW MAPPER
    
    private RowMapper<ProductionReportRow> productionReportRowMapper() {

        return new RowMapper<ProductionReportRow>() {

            @Override
            public ProductionReportRow mapRow(
                    ResultSet rs,
                    int rowNum) throws SQLException {

                ProductionReportRow row =
                        new ProductionReportRow();

                row.setDate(
                        rs.getDate("report_date")
                                .toLocalDate()
                                .toString()
                );

                row.setSku(
                        rs.getString("sku")
                );

                row.setShift(
                        rs.getInt("shift")
                );

                row.setTotalCount(
                        rs.getLong("total_count")
                );

                row.setOkCount(
                        rs.getLong("ok_count")
                );

                row.setNotOkCount(
                        rs.getLong("not_ok_count")
                );

                return row;
            }
        };
    }
}