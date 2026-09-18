package TVS_SFL.BearingHousingBackend.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import TVS_SFL.BearingHousingBackend.services.BearingHousingProductionDataArchiveService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BearingHousingProductionDataArchiveScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BearingHousingProductionDataArchiveScheduler.class);

    private final BearingHousingProductionDataArchiveService productionDataArchiveService;

   // 2am on the 1st and 16th of every month
    @Scheduled(cron = "0 0 2 1,16 * *")
   //@Scheduled(initialDelay = 120000, fixedDelay = Long.MAX_VALUE)
    public void archiveOldProductionData() {
        try {
            int archivedCount = productionDataArchiveService.archiveRecordsOlderThanYears(2);
            logger.info("Database archive job completed. Archived {} records older than 2 years.", archivedCount);
        } catch (Exception ex) {
            logger.error("Database archive job failed.", ex);
        }
    }
}
