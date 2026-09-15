package TVS_SFL.BearingHousingBackend.services.impl;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.repositories.BearingHousingProductionDataArchiveRepository;
import TVS_SFL.BearingHousingBackend.services.BearingHousingProductionDataService;
import TVS_SFL.BearingHousingBackend.constants.SqlQueries;
import TVS_SFL.BearingHousingBackend.dto.DataLocationResult;
import TVS_SFL.BearingHousingBackend.dto.DataLocation;
import TVS_SFL.BearingHousingBackend.dto.PaginatedResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BearingHousingProductionDataServiceImpl implements BearingHousingProductionDataService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BearingHousingProductionDataArchiveRepository archiveRepository;

    @Value("${bearinghousing.photos.directory:photos}")
    private String photoStorageDirectory;

    @Value("${bearinghousing.photos.directories:}")
    private String photoStorageDirectories;

    private static final RowMapper<BearingHousingProductionData> ROW_MAPPER =
            (ResultSet rs, int rowNum) -> {

                BearingHousingProductionData entity = new BearingHousingProductionData();

                entity.setId(rs.getLong("id"));
                entity.setBarcode(rs.getString("barcode"));

                Time cycleStartTime = rs.getTime("cycle_start_time");
                entity.setCycleStartTime(
                        cycleStartTime != null ? cycleStartTime.toLocalTime() : null);

                // Timestamp cycleEndTs = rs.getTimestamp("cycle_end_time");
                // entity.setCycleEndTime(
                //         cycleEndTs != null ? cycleEndTs.toLocalDateTime() : null);

                entity.setCycleTime(rs.getObject("cycle_time", Integer.class));

                Timestamp productionTs = rs.getTimestamp("production_date_time");
                entity.setProductionDateTime(
                        productionTs != null ? productionTs.toLocalDateTime() : null);

                entity.setSku(rs.getString("sku"));

                entity.setNumberofProcess(rs.getObject("number_of_process", Integer.class));

                entity.setShift(rs.getObject("shift", Integer.class));

                entity.setP1_beforeGlueStatus(
                        rs.getObject("p1_before_glue_status", Integer.class));

                entity.setP1_afterGlueStatus(
                        rs.getObject("p1_after_glue_status", Integer.class));

                // entity.setP1_toxLoadMax(
                //         rs.getObject("p1_tox_load_max", Float.class));

                // entity.setP1_toxLoadMin(
                //         rs.getObject("p1_tox_load_min", Float.class));

                entity.setP1_toxLoadActual(
                        rs.getObject("p1_tox_load_actual", Float.class));

                entity.setP1_toxDisplacementMax(
                        rs.getObject("p1_tox_displacement_max", Float.class));

                entity.setP1_toxDisplacementMin(
                        rs.getObject("p1_tox_displacement_min", Float.class));

                entity.setP1_toxDisplacementActual(
                        rs.getObject("p1_tox_displacement_actual", Float.class));

                entity.setP1_graphStatus(
                        rs.getObject("p1_graph_status", Integer.class)); 

                entity.setP2_beforeGlueStatus(
                        rs.getObject("p2_before_glue_status", Integer.class));
                                
                entity.setP2_afterGlueStatus(
                        rs.getObject("p2_after_glue_status", Integer.class));

                // entity.setP2_toxLoadMax(
                //         rs.getObject("p2_tox_load_max", Float.class));

                // entity.setP2_toxLoadMin(
                //         rs.getObject("p2_tox_load_min", Float.class));

                entity.setP2_toxLoadActual(
                        rs.getObject("p2_tox_load_actual", Float.class));

                entity.setP2_toxDisplacementMax(
                        rs.getObject("p2_tox_displacement_max", Float.class));

                entity.setP2_toxDisplacementMin(
                        rs.getObject("p2_tox_displacement_min", Float.class));

                entity.setP2_toxDisplacementActual(
                        rs.getObject("p2_tox_displacement_actual", Float.class));

                entity.setP2_graphStatus(
                        rs.getObject("p2_graph_status", Integer.class));

                entity.setCupConsumed(
                        rs.getObject("cup_consumed", Integer.class));                

                entity.setFinalStatus(
                        rs.getObject("final_status", Integer.class));

                entity.setTotalPartCount(
                        rs.getObject("total_part_count", Integer.class));

                entity.setOkCount(
                        rs.getObject("ok_count", Integer.class));

                entity.setNotOkCount(
                        rs.getObject("not_ok_count", Integer.class));

                entity.setOperatorName(rs.getString("operator_name"));

                return entity;
            };

    private List<BearingHousingProductionData> mergeLiveAndArchive(
            List<BearingHousingProductionData> liveData,
            List<BearingHousingProductionData> archiveData) {

        Map<Long, BearingHousingProductionData> merged = new LinkedHashMap<>();

        if (liveData != null) {
            for (BearingHousingProductionData item : liveData) {
                if (item != null) {
                    merged.put(item.getId(), item);
                }
            }
        }

        if (archiveData != null) {
            for (BearingHousingProductionData item : archiveData) {
                if (item != null) {
                    merged.putIfAbsent(item.getId(), item);
                }
            }
        }

        return new ArrayList<>(merged.values());
    }

    @Override
    public List<BearingHousingProductionData> getAllProductionData() {
        List<BearingHousingProductionData> liveData = jdbcTemplate.query(SqlQueries.SELECT_ALL, ROW_MAPPER);
        List<BearingHousingProductionData> archiveData = archiveRepository.findByFinalStatus(1);
        return mergeLiveAndArchive(liveData, archiveData);
    }

    @Override
    public BearingHousingProductionData getProductionDataByBarcode(String barcode) {
        List<BearingHousingProductionData> results =
                jdbcTemplate.query(SqlQueries.SELECT_BY_BARCODE, ROW_MAPPER, barcode);

        if (!results.isEmpty()) {
            return results.get(0);
        }

        return archiveRepository.findByBarcode(barcode);
    }

    @Override
    public BearingHousingProductionData getProductionDataByBarcodeFromArchive(String barcode) {
        return archiveRepository.findByBarcode(barcode);
    }

    @Override
    public DataLocationResult getProductionDataLocation(String barcode) {
        BearingHousingProductionData liveRecord = getProductionDataByBarcode(barcode);
        if (liveRecord != null) {
            return new DataLocationResult(DataLocation.LIVE, liveRecord);
        }

        BearingHousingProductionData archiveRecord = getProductionDataByBarcodeFromArchive(barcode);
        if (archiveRecord != null) {
            return new DataLocationResult(DataLocation.ARCHIVED, archiveRecord);
        }

        return new DataLocationResult(DataLocation.NOT_FOUND, null);
    }

    @Override
    public BearingHousingProductionData getProductionDataAndPhotosByBarcode(String barcode) {
        List<BearingHousingProductionData> results =
                jdbcTemplate.query(SqlQueries.SELECT_BY_BARCODE, ROW_MAPPER, barcode);

        if (!results.isEmpty()) {
            return results.get(0);
        }

        return archiveRepository.findByBarcode(barcode);
    }

    @Override
    public BearingHousingProductionData saveProductionData(
            BearingHousingProductionData entity) {

        Timestamp cycleStartTs =
                entity.getCycleStartTime() != null
                        ? Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.of(1970, 1, 1), entity.getCycleStartTime()))
                        : null;

        // Timestamp cycleEndTs =
        //         entity.getCycleEndTime() != null
        //                 ? Timestamp.valueOf(entity.getCycleEndTime())
        //                 : null;

        Timestamp productionTs =
                entity.getProductionDateTime() != null
                        ? Timestamp.valueOf(entity.getProductionDateTime())
                        : null;

        int rowsAffected = jdbcTemplate.update(
                SqlQueries.INSERT,
                entity.getBarcode(),
                cycleStartTs,
                // cycleEndTs,
                entity.getCycleTime(),
                productionTs,
                entity.getSku(),
                entity.getShift(),
                entity.getNumberofProcess(),
                entity.getP1_beforeGlueStatus(),
                entity.getP1_afterGlueStatus(),
                // entity.getP1_toxLoadMax(),
                // entity.getP1_toxLoadMin(),
                entity.getP1_toxLoadActual(),
                entity.getP1_toxDisplacementMax(),
                entity.getP1_toxDisplacementMin(),
                entity.getP1_toxDisplacementActual(),
                entity.getP1_graphStatus(),
                entity.getP2_beforeGlueStatus(),
                entity.getP2_afterGlueStatus(),
                // entity.getP2_toxLoadMax(),
                // entity.getP2_toxLoadMin(),
                entity.getP2_toxLoadActual(),
                entity.getP2_toxDisplacementMax(),
                entity.getP2_toxDisplacementMin(),
                entity.getP2_toxDisplacementActual(),
                entity.getP2_graphStatus(),
                entity.getCupConsumed(),
                entity.getFinalStatus(),
                entity.getTotalPartCount(),
                entity.getOkCount(),
                entity.getNotOkCount(),
                entity.getOperatorName());

        if (rowsAffected > 0) {
            List<BearingHousingProductionData> results =
                    jdbcTemplate.query(
                            SqlQueries.SELECT_LAST_INSERTED,
                            ROW_MAPPER);

            return results.isEmpty() ? null : results.get(0);
        }

        return null;
    }

    @Override
    public BearingHousingProductionData updateProductionData(
            String barcode,
            BearingHousingProductionData entity) {

        Timestamp cycleStartTs =
                entity.getCycleStartTime() != null
                        ? Timestamp.valueOf(java.time.LocalDateTime.of(java.time.LocalDate.of(1970, 1, 1), entity.getCycleStartTime()))
                        : null;

        // Timestamp cycleEndTs =
        //         entity.getCycleEndTime() != null
        //                 ? Timestamp.valueOf(entity.getCycleEndTime())
        //                 : null;

        Timestamp productionTs =
                entity.getProductionDateTime() != null
                        ? Timestamp.valueOf(entity.getProductionDateTime())
                        : null;

        int rowsAffected = jdbcTemplate.update(
                SqlQueries.UPDATE_BY_ID,
                entity.getBarcode(),
                cycleStartTs,
                // cycleEndTs,
                entity.getCycleTime(),
                productionTs,
                entity.getSku(),
                entity.getShift(),
                entity.getNumberofProcess(),
                entity.getP1_beforeGlueStatus(),
                entity.getP1_afterGlueStatus(),
                // entity.getP1_toxLoadMax(),
                // entity.getP1_toxLoadMin(),
                entity.getP1_toxLoadActual(),
                entity.getP1_toxDisplacementMax(),
                entity.getP1_toxDisplacementMin(),
                entity.getP1_toxDisplacementActual(),
                entity.getP1_graphStatus(),
                entity.getP2_beforeGlueStatus(),
                entity.getP2_afterGlueStatus(),
                // entity.getP2_toxLoadMax(),
                // entity.getP2_toxLoadMin(),
                entity.getP2_toxLoadActual(),
                entity.getP2_toxDisplacementMax(),
                entity.getP2_toxDisplacementMin(),
                entity.getP2_toxDisplacementActual(),
                entity.getP2_graphStatus(),
                entity.getCupConsumed(),
                entity.getFinalStatus(),
                entity.getTotalPartCount(),
                entity.getOkCount(),
                entity.getNotOkCount(),
                entity.getOperatorName(),
                barcode);

        if (rowsAffected > 0) {
            return getProductionDataByBarcode(barcode);
        }

        return null;
    }

    @Override
    public void deleteProductionData(String barcode) {
        jdbcTemplate.update(SqlQueries.DELETE_BY_ID, barcode);
    }

    @Override
    public List<BearingHousingProductionData> getProductionDataBySku(String sku) {
        List<BearingHousingProductionData> liveData = jdbcTemplate.query(
                SqlQueries.SELECT_BY_SKU,
                ROW_MAPPER,
                sku);
        List<BearingHousingProductionData> archiveData = archiveRepository.findBySku(sku);
        return mergeLiveAndArchive(liveData, archiveData);
    }

    @Override
    public List<BearingHousingProductionData> getProductionDataByOperatorName(
            String operatorName) {

        List<BearingHousingProductionData> liveData = jdbcTemplate.query(
                SqlQueries.SELECT_BY_OPERATOR,
                ROW_MAPPER,
                operatorName);
        List<BearingHousingProductionData> archiveData = archiveRepository.findByOperatorName(operatorName);
        return mergeLiveAndArchive(liveData, archiveData);
    }

    @Override
    public List<BearingHousingProductionData> getProductionDataByShift(
            Integer shift) {

        List<BearingHousingProductionData> liveData = jdbcTemplate.query(
                SqlQueries.SELECT_BY_SHIFT,
                ROW_MAPPER,
                shift);
        List<BearingHousingProductionData> archiveData = archiveRepository.findByShift(shift);
        return mergeLiveAndArchive(liveData, archiveData);
    }

    @Override
    public BearingHousingProductionData getLatestProductionDataByShift(
            Integer shift) {

        List<BearingHousingProductionData> data = jdbcTemplate.query(
                SqlQueries.SELECT_LATEST_BY_SHIFT,
                ROW_MAPPER,
                shift);

        if (!data.isEmpty()) {
            return data.get(0);
        }

        return archiveRepository.findLatestByShift(shift);
    }

        @Override
        public BearingHousingProductionData getLatestProductionData() {
                List<BearingHousingProductionData> data = jdbcTemplate.query(
                                SqlQueries.SELECT_LATEST,
                                ROW_MAPPER);

                if (!data.isEmpty()) {
                        return data.get(0);
                }

                return archiveRepository.findLatest();
        }

    @Override
    public List<BearingHousingProductionData> getProductionDataByFinalStatus(
            Integer status) {

        List<BearingHousingProductionData> liveData = jdbcTemplate.query(
                SqlQueries.SELECT_BY_FINAL_STATUS,
                ROW_MAPPER,
                status);
        List<BearingHousingProductionData> archiveData = archiveRepository.findByFinalStatus(status);
        return mergeLiveAndArchive(liveData, archiveData);
    }

    @Override
    public List<String> getPhotoFileNamesByBarcode(String barcode) {

        List<String> dirs;

        if (photoStorageDirectories != null &&
                !photoStorageDirectories.trim().isEmpty()) {

            dirs = Stream.of(photoStorageDirectories.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

        } else {
            dirs = List.of(photoStorageDirectory);
        }

        Set<String> results = new LinkedHashSet<>();

        for (int i = 0; i < dirs.size(); i++) {

            String dir = dirs.get(i);

            Path photosRoot = Paths.get(dir);
            Path barcodeFolder =
                    photosRoot.resolve(String.valueOf(barcode));

            if (!Files.exists(barcodeFolder) ||
                    !Files.isDirectory(barcodeFolder)) {
                continue;
            }

            try (Stream<Path> fileStream = Files.list(barcodeFolder)) {

                List<String> names = fileStream
                        .filter(path ->
                                Files.isRegularFile(path)
                                        && path.getFileName()
                                        .toString()
                                        .toLowerCase()
                                        .endsWith(".png"))
                        .map(path -> path.getFileName().toString())
                        .sorted()
                        .collect(Collectors.toList());

                for (String name : names) {
                    results.add(i + "__" + name);
                }

            } catch (IOException ex) {
                // Ignore and continue
            }
        }

        return new ArrayList<>(results);
    }

    @Override
    public PaginatedResponse<BearingHousingProductionData> getProductionDataByDateRange(
            LocalDate startDate, LocalDate endDate, Integer shift, String sku, int page, int size) {

        int offset = page * size;
        String combinedSource = """
                FROM (
                    SELECT id, barcode, operator_name, shift, sku, number_of_process,
                           cycle_start_time, cycle_time, production_date_time,
                           p1_before_glue_status, p1_after_glue_status,
                           p1_tox_load_actual, p1_tox_displacement_max,
                           p1_tox_displacement_min, p1_tox_displacement_actual,
                           p1_graph_status, p2_before_glue_status, p2_after_glue_status,
                           p2_tox_load_actual, p2_tox_displacement_max,
                           p2_tox_displacement_min, p2_tox_displacement_actual,
                           p2_graph_status, cup_consumed, final_status,
                           ok_count, not_ok_count, total_part_count
                    FROM bearing_housing_production_data
                    UNION ALL
                    SELECT id, barcode, operator_name, shift, sku, number_of_process,
                           cycle_start_time, cycle_time, production_date_time,
                           p1_before_glue_status, p1_after_glue_status,
                           p1_tox_load_actual, p1_tox_displacement_max,
                           p1_tox_displacement_min, p1_tox_displacement_actual,
                           p1_graph_status, p2_before_glue_status, p2_after_glue_status,
                           p2_tox_load_actual, p2_tox_displacement_max,
                           p2_tox_displacement_min, p2_tox_displacement_actual,
                           p2_graph_status, cup_consumed, final_status,
                           ok_count, not_ok_count, total_part_count
                    FROM bearing_housing_production_data_archive
                ) AS combined_production_data
                WHERE production_date_time >= ?
                  AND production_date_time < ?
                """;

        List<Object> filterParams = new ArrayList<>();
        filterParams.add(startDate.atStartOfDay());
        filterParams.add(endDate.plusDays(1).atStartOfDay());

        StringBuilder filters = new StringBuilder();
        if (shift != null) {
            filters.append(" AND shift = ?");
            filterParams.add(shift);
        }
        if (sku != null && !sku.trim().isEmpty() && !sku.equalsIgnoreCase("ALL") && !sku.equals("0")) {
            filters.append(" AND sku = ?");
            filterParams.add(sku);
        }

        String selectSql = "SELECT * " + combinedSource + filters
                + " ORDER BY production_date_time DESC LIMIT ? OFFSET ?";
        List<Object> selectParams = new ArrayList<>(filterParams);
        selectParams.add(size);
        selectParams.add(offset);

        List<BearingHousingProductionData> data = jdbcTemplate.query(
                selectSql, ROW_MAPPER, selectParams.toArray());

        String countSql = "SELECT COUNT(*) " + combinedSource + filters;
        long totalElements = jdbcTemplate.queryForObject(
                countSql, Long.class, filterParams.toArray());

        return new PaginatedResponse<>(data, page, size, totalElements);
    }   
}



