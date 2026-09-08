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
import TVS_SFL.BearingHousingBackend.dto.CupConsumptionReportRow;

@Repository
@RequiredArgsConstructor
public class CupConsumptionReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<CupConsumptionReportRow> getDayReport(
            String from,
            String to,
            Integer shift) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDateExclusive = LocalDate.parse(to).plusDays(1);
        String sql = """
              SELECT production_date_time::date AS report_period,
                       shift,
                  COALESCE(MAX(cup_consumed), 0) AS cup_consumed
                FROM bearing_housing_production_data
              WHERE production_date_time >= ?::timestamp
                  AND production_date_time < ?::timestamp
                """;

        List<Object> params = new ArrayList<>();
        params.add(fromDate.atStartOfDay());
        params.add(toDateExclusive.atStartOfDay());
        return query(sql, params, shift, "production_date_time::date");
    }

    public List<CupConsumptionReportRow> getMonthReport(
            int year,
            Integer month,
            Integer shift) {

        LocalDate start = month != null && month > 0
                ? LocalDate.of(year, month, 1)
                : LocalDate.of(year, 1, 1);
        LocalDate end = month != null && month > 0
                ? start.plusMonths(1)
                : start.plusYears(1);
        List<Object> params = new ArrayList<>();
        params.add(start.atStartOfDay());
        params.add(end.atStartOfDay());
        return queryCumulativePeriod(
            "DATE_TRUNC('month', production_day)::date",
            params,
            shift
        );
    }

    public List<CupConsumptionReportRow> getYearReport(
            int year,
            Integer shift) {

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);
        List<Object> params = new ArrayList<>();
        params.add(start.atStartOfDay());
        params.add(end.atStartOfDay());
        return queryCumulativePeriod(
                "DATE_TRUNC('year', production_day)::date",
                params,
                shift
        );
    }

    private List<CupConsumptionReportRow> queryCumulativePeriod(
            String periodExpression,
            List<Object> params,
            Integer shift) {

        StringBuilder sql = new StringBuilder("""
                SELECT %s AS report_period,
                       shift,
                       COALESCE(SUM(daily_cup_consumed), 0) AS cup_consumed
                FROM (
                    SELECT production_date_time::date AS production_day,
                           shift,
                           COALESCE(MAX(cup_consumed), 0) AS daily_cup_consumed
                    FROM bearing_housing_production_data
                    WHERE production_date_time >= ?::timestamp
                      AND production_date_time < ?::timestamp
                """.formatted(periodExpression));

        if (shift != null && shift > 0) {
            sql.append(" AND shift = ? ");
            params.add(shift);
        }

        sql.append(" GROUP BY production_date_time::date, shift) daily");
        sql.append(" GROUP BY report_period, shift ORDER BY report_period, shift");

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper());
    }

    private List<CupConsumptionReportRow> query(
            String baseSql,
            List<Object> params,
            Integer shift,
            String groupPeriod) {

        StringBuilder sql = new StringBuilder(baseSql);
        if (shift != null && shift > 0) {
            sql.append(" AND shift = ? ");
            params.add(shift);
        }

        sql.append(" GROUP BY ").append(groupPeriod).append(", shift");
        sql.append(" ORDER BY report_period, shift");

        return jdbcTemplate.query(sql.toString(), params.toArray(), rowMapper());
    }

    private RowMapper<CupConsumptionReportRow> rowMapper() {
        return new RowMapper<CupConsumptionReportRow>() {
            @Override
            public CupConsumptionReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CupConsumptionReportRow(
                        rs.getDate("report_period").toLocalDate().toString(),
                        rs.getInt("shift"),
                        rs.getLong("cup_consumed")
                );
            }
        };
    }
}
