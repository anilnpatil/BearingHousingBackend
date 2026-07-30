package TVS_SFL.BearingHousingBackend.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShiftReportExportService {

    private static final String SELECT_SHIFT_REPORT_SQL =
            "SELECT * FROM bearing_housing_production_data " +
            "WHERE shift = ? AND production_date_time >= ? AND production_date_time < ? " +
            "ORDER BY production_date_time ASC";

    private static final String[] CSV_HEADERS = {
            "id",
            "barcode",
            "cycle_start_time",
            //"cycle_end_time",
            "cycle_time",
            "production_date_time",
            "sku",
            "number_of_process",
            "shift",
            "p1_before_glue_status",
            "p1_after_glue_status",
            "p1_tox_load_max",
            "p1_tox_load_min",
            "p1_tox_load_actual",
            "p1_tox_displacement_max",
            "p1_tox_displacement_min",
            "p1_tox_displacement_actual",
            "p2_before_glue_status",
            "p2_after_glue_status",
            "p2_tox_load_max",
            "p2_tox_load_min",
            "p2_tox_load_actual",
            "p2_tox_displacement_max",
            "p2_tox_displacement_min",
            "p2_tox_displacement_actual",
            "final_status",
            "total_part_count",
            "ok_count",
            "not_ok_count",
            "operator_name"
    };

    private final JdbcTemplate jdbcTemplate;

    @Value("${report.base-dir:C:/Reports}")
    private String reportBaseDir;

    public ShiftReportExportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void generateShiftReport(int shift, LocalDateTime startTime, LocalDateTime endTime) {
        Path outputDir = resolveOutputDirectory(shift, startTime.toLocalDate());
        Path outputFile = outputDir.resolve(
                String.format(
                        "shift%d_%s.csv",
                        shift,
                        startTime.toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE))
        );

        try {
            Files.createDirectories(outputDir);

            AtomicBoolean headerWritten = new AtomicBoolean(false);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    outputFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                jdbcTemplate.query(connection -> {
                    PreparedStatement ps = connection.prepareStatement(SELECT_SHIFT_REPORT_SQL);
                    ps.setInt(1, shift);
                    ps.setTimestamp(2, Timestamp.valueOf(startTime));
                    ps.setTimestamp(3, Timestamp.valueOf(endTime));
                    return ps;
                }, rs -> {
                    try {
                        if (!headerWritten.get()) {
                            writer.write(String.join(",", CSV_HEADERS));
                            writer.newLine();
                            headerWritten.set(true);
                        }

                        writer.write(buildCsvRow(rs));
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to write report row", e);
                    }
                });
            }

            System.out.println("Report generated: " + outputFile);
        } catch (IOException e) {
            System.err.println("Failed to create report directory/file for shift " + shift);
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Failed to generate report for shift " + shift);
            e.printStackTrace();
        }
    }

    private Path resolveOutputDirectory(int shift, LocalDate date) {
        Path basePath = Paths.get(reportBaseDir).toAbsolutePath().normalize();
        return basePath
                .resolve("shift" + shift)
                .resolve(String.format("%04d", date.getYear()))
                .resolve(String.format("%02d", date.getMonthValue()))
                .resolve(String.format("%02d", date.getDayOfMonth()));
    }

    private String buildCsvRow(ResultSet rs) throws SQLException {
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < CSV_HEADERS.length; i++) {
            if (i > 0) {
                row.append(",");
            }
            row.append(escapeCsv(rs.getString(CSV_HEADERS[i])));
        }

        return row.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return '"' + escaped + '"';
        }

        return escaped;
    }
}
