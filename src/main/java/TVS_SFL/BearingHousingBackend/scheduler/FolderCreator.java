package TVS_SFL.BearingHousingBackend.scheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class FolderCreator {

    private static final String ROOT_PATH =
            "C:/current working directroy/bearingHoushingPhotos";

    private static final String[] SHIFTS = {
            "shift1",
            "shift2",
            "shift3"
    };

    /**
     * Runs once when application starts
     */
    @PostConstruct
    public void createFoldersOnStartup() {

        System.out.println("Creating folders on application startup...");

        createFoldersForNextWeek();
    }

    /**
     * Runs every day at 12:05 AM
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void createFoldersForNextWeek() {

        LocalDate today = LocalDate.now();

        for (int i = 0; i <= 7; i++) {

            LocalDate date = today.plusDays(i);

            createDateFolders(date);
        }
    }

    private void createDateFolders(LocalDate date) {

        String year =
                String.format("%04d", date.getYear());

        String month =
                String.format("%02d", date.getMonthValue());

        String day =
                String.format("%02d", date.getDayOfMonth());

        for (String shift : SHIFTS) {

            Path folder = Path.of(
                    ROOT_PATH,
                    shift,
                    year,
                    month,
                    day);

            try {

                if (!Files.exists(folder)) {

                    Files.createDirectories(folder);

                    System.out.println(
                            "Created Folder : " + folder);
                }

            } catch (IOException e) {

                System.err.println(
                        "Failed to create folder : " + folder);

                e.printStackTrace();
            }
        }
    }
}