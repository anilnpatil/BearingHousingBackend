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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShiftReportExportService {

    private static final String SELECT_SHIFT_REPORT_SQL =
            "SELECT * FROM bearing_housing_production_data " +
            "WHERE shift = ? AND production_date_time >= ? AND production_date_time < ? " +
            "ORDER BY production_date_time ASC, id ASC";

        private static final String CREATE_EXPORT_TRACKING_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS shift_report_exports (
            report_date DATE NOT NULL,
            shift INTEGER NOT NULL,
            output_file VARCHAR(500) NOT NULL,
            record_count INTEGER NOT NULL,
            exported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            PRIMARY KEY (report_date, shift)
            )
            """;

    private static final DateTimeFormatter CSV_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final String[] CSV_HEADERS = {
            // "id",            
            "production_date_time",
            "cycle_start_time",
            "barcode",            
            //"cycle_end_time",                       
            "sku",
            "number_of_process",
            "shift",
            "p1_before_glue_status",
            "p1_after_glue_status",
            // "p1_tox_load_max",
            // "p1_tox_load_min",
            "p1_tox_load_actual",
            "p1_tox_displacement_max",
            "p1_tox_displacement_actual",
            "p1_tox_displacement_min",            
            "p2_before_glue_status",
            "p2_after_glue_status",
            // "p2_tox_load_max",
            // "p2_tox_load_min",            
            "p2_tox_displacement_max",
            "p2_tox_load_actual",
            "p2_tox_displacement_min",
            "p2_tox_displacement_actual",
            "final_status",
            "total_part_count",
            "ok_count",
            "not_ok_count",
            "cup_consumed",
            "operator_name",
            "cycle_time"
    };

    private final JdbcTemplate jdbcTemplate;

    @Value("${report.base-dir:C:/Reports}")
    private String reportBaseDir;

    public ShiftReportExportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void generateShiftReport(int shift, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate reportDate = shift == 3 ? endTime.toLocalDate() : startTime.toLocalDate();
        Path outputDir = resolveOutputDirectory(shift, reportDate);
        Path outputFile = outputDir.resolve(
                String.format(
                        "shift%d_%s.csv",
                        shift,
                reportDate.format(DateTimeFormatter.BASIC_ISO_DATE))
        );

        try {
            ensureExportTrackingTable();
            if (isReportAlreadyExported(shift, reportDate, outputFile)) {
                System.out.println("Report already exported: " + outputFile);
                return;
            }

            Files.createDirectories(outputDir);

            Path temporaryFile = Files.createTempFile(outputDir, outputFile.getFileName().toString(), ".tmp");
            AtomicInteger rowCount = new AtomicInteger();

            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporaryFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                writer.write(String.join(",", CSV_HEADERS));
                writer.newLine();

                jdbcTemplate.query(connection -> {
                    PreparedStatement ps = connection.prepareStatement(SELECT_SHIFT_REPORT_SQL);
                    ps.setInt(1, shift);
                    ps.setTimestamp(2, Timestamp.valueOf(startTime));
                    ps.setTimestamp(3, Timestamp.valueOf(endTime));
                    return ps;
                }, rs -> {
                    try {
                        writer.write(buildCsvRow(rs));
                        writer.newLine();
                        rowCount.incrementAndGet();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to write report row", e);
                    }
                });
            }

            try {
                Files.move(temporaryFile, outputFile, java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, outputFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            recordExportedReport(shift, reportDate, outputFile, rowCount.get());
            System.out.printf("Report generated: %s (%d records)%n", outputFile, rowCount.get());
        } catch (IOException e) {
            System.err.println("Failed to create report directory/file for shift " + shift);
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Failed to generate report for shift " + shift);
            e.printStackTrace();
        }
    }

    private void ensureExportTrackingTable() {
        jdbcTemplate.execute(CREATE_EXPORT_TRACKING_TABLE_SQL);
    }

    public void generateMissingReports(LocalDateTime now) {
        ensureExportTrackingTable();

        LocalDate lastReportDate = now.toLocalDate();
        LocalDate firstReportDate = lastReportDate.minusDays(3);

        for (LocalDate reportDate = firstReportDate;
            !reportDate.isAfter(lastReportDate);
                reportDate = reportDate.plusDays(1)) {
            generateIfWindowCompleted(1, reportDate,
                LocalDateTime.of(reportDate, LocalTime.of(8, 30)),
                LocalDateTime.of(reportDate, LocalTime.of(17, 30)), now);
            generateIfWindowCompleted(2, reportDate,
                LocalDateTime.of(reportDate, LocalTime.of(17, 30)),
                LocalDateTime.of(reportDate.plusDays(1), LocalTime.of(1, 30)), now);
            generateIfWindowCompleted(3, reportDate,
                LocalDateTime.of(reportDate, LocalTime.of(1, 30)),
                LocalDateTime.of(reportDate, LocalTime.of(8, 30)), now);
        }
    }

    private void generateIfWindowCompleted(int shift, LocalDate reportDate,
            LocalDateTime startTime, LocalDateTime endTime, LocalDateTime now) {
        if (!endTime.isAfter(now)) {
            generateShiftReport(shift, startTime, endTime);
        }
    }

    private boolean isReportAlreadyExported(int shift, LocalDate reportDate, Path outputFile) {
        if (!Files.isRegularFile(outputFile)) {
            return false;
        }

        Integer exported = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM shift_report_exports WHERE report_date = ? AND shift = ?",
                Integer.class,
                reportDate,
                shift);
        return exported != null && exported > 0;
    }

    private void recordExportedReport(int shift, LocalDate reportDate, Path outputFile, int recordCount) {
        jdbcTemplate.update("""
                INSERT INTO shift_report_exports (report_date, shift, output_file, record_count)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (report_date, shift) DO UPDATE SET
                    output_file = EXCLUDED.output_file,
                    record_count = EXCLUDED.record_count,
                    exported_at = CURRENT_TIMESTAMP
                """,
                reportDate,
                shift,
                outputFile.toString(),
                recordCount);
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
            row.append(escapeCsv(readCsvValue(rs, CSV_HEADERS[i])));
        }

        return row.toString();
    }

    private String readCsvValue(ResultSet rs, String column) throws SQLException {
        if ("production_date_time".equals(column)) {
            Timestamp timestamp = rs.getTimestamp(column);
            return timestamp == null
                    ? null
                    : timestamp.toLocalDateTime().format(CSV_DATE_TIME_FORMATTER);
        }

        return rs.getString(column);
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
