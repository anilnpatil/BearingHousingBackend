package TVS_SFL.BearingHousingBackend.scheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import TVS_SFL.BearingHousingBackend.repositories.BearingHousingImageRepository;
import lombok.RequiredArgsConstructor;
import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class OldFileArchiver {

        private static final Path ARCHIVE_ROOT =
            Path.of("C:/BearingHousingOldFiles");
    private static final List<Path> LIVE_ROOTS = List.of(
            Path.of("C:/BearingHousingImages/IV-4"),
            Path.of("C:/BearingHousingGraphs")
    );
    private static final String SHIFT_PREFIX = "Shift";
    private static final int ARCHIVE_DAYS = 90;

    private final BearingHousingImageRepository repository;

    @PostConstruct
    public void moveOldFilesOnStartup() {
        System.out.println("Starting old file archive migration on startup...");
        moveOldFilesToArchive();
    }

    //@Scheduled(cron = "0 15 0 * * *")
    @Scheduled(cron = "0 45 17 * * *")
    public void moveOldFilesToArchive() {
        LocalDate cutoff = LocalDate.now().minusDays(ARCHIVE_DAYS);
        List<BearingHousingProductionData> oldRecords = repository.findProductionDataOlderThan(cutoff);

        if (oldRecords == null || oldRecords.isEmpty()) {
            System.out.println("No production records older than " + cutoff + " found.");
            return;
        }

        Map<String, BearingHousingProductionData> oldRecordByBarcode = oldRecords.stream()
                .collect(Collectors.toMap(BearingHousingProductionData::getBarcode, data -> data));

        // For each live root, find files whose filename starts with a barcode of an old record
        for (Path liveRoot : LIVE_ROOTS) {
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

    private Set<String> buildExpectedFileNames(BearingHousingProductionData data) {
        String date = String.format("%02d%02d%02d",
                data.getProductionDateTime().getDayOfMonth(),
                data.getProductionDateTime().getMonthValue(),
                data.getProductionDateTime().getYear() % 100);

        String shift = "S" + data.getShift();
        String barcode = data.getBarcode();

        Set<String> fileNames = Set.of(
                barcode + "_P1_" + (data.getP1_beforeGlueStatus() == 1 ? "BK" : "BN") + "_" + shift + "-" + date + ".jpeg",
                barcode + "_P1_" + (data.getP1_afterGlueStatus() == 1 ? "AK" : "AN") + "_" + shift + "-" + date + ".jpeg",
                barcode + "_P1_" + shift + "-" + date + ".pdf"
        );

        if (data.getNumberofProcess() != null && data.getNumberofProcess() >= 2) {
            return Stream.concat(fileNames.stream(), Stream.of(
                    barcode + "_P2_" + (data.getP2_beforeGlueStatus() == 1 ? "BK" : "BN") + "_" + shift + "-" + date + ".jpeg",
                    barcode + "_P2_" + (data.getP2_afterGlueStatus() == 1 ? "AK" : "AN") + "_" + shift + "-" + date + ".jpeg",
                    barcode + "_P2_" + shift + "-" + date + ".pdf"
            )).collect(Collectors.toSet());
        }

        return fileNames;
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

        Path target = buildArchiveFolder(data, fileName);
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
        String shiftFolder = SHIFT_PREFIX + data.getShift();
        String year = String.format("%04d", productionDate.getYear());
        String month = String.format("%02d", productionDate.getMonthValue());
        String day = String.format("%02d", productionDate.getDayOfMonth());

        return ARCHIVE_ROOT.resolve(shiftFolder)
                .resolve(year)
                .resolve(month)
                .resolve(day)
                .resolve(data.getBarcode())
                .resolve(fileName);
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
