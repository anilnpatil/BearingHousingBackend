package TVS_SFL.BearingHousingBackend.scheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import TVS_SFL.BearingHousingBackend.config.FilePathConfig;
import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.repositories.BearingHousingImageRepository;
import lombok.RequiredArgsConstructor;
import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class OldFileArchiver {

    @Value("${file.archive-root}")
    private String archiveRoot;

    @Value("${file.live-image-root}")
    private String liveImageRoot;

    @Value("${file.live-graph-root}")
    private String liveGraphRoot;

    @Value("${file.shift-prefix}")
    private String shiftPrefix;

    @Value("${file.archive-threshold-days}")
    private int archiveDays;

    private final BearingHousingImageRepository repository;

    @PostConstruct
    public void moveOldFilesOnStartup() {
        System.out.println("Starting old file archive migration on startup...");
        moveOldFilesToArchive();
    }

    //@Scheduled(cron = "0 15 0 * * *")
    @Scheduled(cron = "0 53 16 * * *")
    public void moveOldFilesToArchive() {
        LocalDate cutoff = LocalDate.now().minusDays(archiveDays);
        List<BearingHousingProductionData> oldRecords = repository.findProductionDataOlderThan(cutoff);

        if (oldRecords == null || oldRecords.isEmpty()) {
            System.out.println("No production records older than " + cutoff + " found.");
            return;
        }

        Map<String, BearingHousingProductionData> oldRecordByBarcode = oldRecords.stream()
                .collect(Collectors.toMap(BearingHousingProductionData::getBarcode, data -> data));

        List<Path> liveRoots = List.of(
                FilePathConfig.toPath(liveImageRoot),
                FilePathConfig.toPath(liveGraphRoot)
        );

        // For each live root, find files whose filename starts with a barcode of an old record.
        // File names use DDMMYYYY; archive folders use YYYY/MM/DD.
        for (Path liveRoot : liveRoots) {
            try (Stream<Path> entries = Files.walk(liveRoot)) {
                entries.filter(Files::isRegularFile)
                        .filter(path -> isSupportedFile(path.getFileName().toString()))
                        .forEach(path -> {
                            String fileName = path.getFileName().toString();
                            String barcode = extractBarcode(fileName);
                            if (barcode != null && oldRecordByBarcode.containsKey(barcode)) {
                                archivePath(path, oldRecordByBarcode);
                            }
                        });
            } catch (IOException e) {
                System.err.println("Unable to read storage directory " + liveRoot + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Old file archive migration completed.");
    }

    private void archivePath(Path file, Map<String, BearingHousingProductionData> oldRecordByBarcode) {
        String fileName = file.getFileName().toString();
        String barcode = extractBarcode(fileName);
        if (barcode == null) {
            System.err.println("Unable to extract barcode from file name: " + fileName);
            return;
        }

        BearingHousingProductionData data = oldRecordByBarcode.get(barcode);
        if (data == null) {
            System.err.println("Skipping archival for " + fileName + ": no cached metadata for barcode " + barcode);
            return;
        }

        String archiveFileName = normalizeFileNameYear(fileName, data.getProductionDateTime().toLocalDate());
        Path target = buildArchiveFolder(data, archiveFileName);
        try {
            Files.createDirectories(target.getParent());
            Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved file " + fileName + " to " + target.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to move " + fileName + " to " + target.toAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Path buildArchiveFolder(BearingHousingProductionData data, String fileName) {
        LocalDate productionDate = data.getProductionDateTime().toLocalDate();
        Integer fileShift = extractShift(fileName);
        String shiftFolder = shiftPrefix + (fileShift != null ? fileShift : data.getShift());
        String year = String.format("%04d", productionDate.getYear());
        String month = String.format("%02d", productionDate.getMonthValue());
        String day = String.format("%02d", productionDate.getDayOfMonth());

        return FilePathConfig.toPath(archiveRoot)
                .resolve(shiftFolder)
                .resolve(year)
                .resolve(month)
                .resolve(day)
                .resolve(data.getBarcode())
                .resolve(fileName);
    }

    private String normalizeFileNameYear(String fileName, LocalDate productionDate) {
        String extension = "";
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex >= 0) {
            extension = fileName.substring(extensionIndex);
        }

        String baseName = extension.isEmpty()
                ? fileName
                : fileName.substring(0, extensionIndex);

        if (baseName.length() < 6) {
            return fileName;
        }

        String dateCode = baseName.substring(baseName.length() - 6);
        if (!dateCode.matches("\\d{6}")) {
            return fileName;
        }

        String fourDigitDate = dateCode.substring(0, 4) + productionDate.getYear();
        return baseName.substring(0, baseName.length() - 6) + fourDigitDate + extension;
    }

    private Integer extractShift(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        for (String part : fileName.split("_")) {
            if (part.length() > 1 && (part.charAt(0) == 'S' || part.charAt(0) == 's')) {
                try {
                    return Integer.valueOf(part.substring(1));
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private String extractBarcode(String fileName) {
        int delimiter = fileName.indexOf('_');
        if (delimiter <= 0) {
            return null;
        }
        return fileName.substring(0, delimiter);
    }

    private boolean isSupportedFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpeg") || lower.endsWith(".jpg") || lower.endsWith(".png")
                || lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp")
                || lower.endsWith(".pdf");
    }
}
