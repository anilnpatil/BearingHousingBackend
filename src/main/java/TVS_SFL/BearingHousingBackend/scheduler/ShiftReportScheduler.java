package TVS_SFL.BearingHousingBackend.scheduler;

import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import TVS_SFL.BearingHousingBackend.services.ShiftReportExportService;

@Component
public class ShiftReportScheduler {

    private final ShiftReportExportService reportExportService;

    public ShiftReportScheduler(ShiftReportExportService reportExportService) {
        this.reportExportService = reportExportService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateMissingReportsOnStartup() {
        reportExportService.generateMissingReports(LocalDateTime.now());
    }

    /**
     * Runs at the end of each shift.
     * Shift 1: 08:30 to 17:30
     * Shift 2: 17:30 to 01:30
     * Shift 3: 01:30 to 08:30
     */    
    
    @Scheduled(cron = "21 30 17 * * *")
    public void generateShift1Report() {
        reportExportService.generateMissingReports(LocalDateTime.now());
    }

    
    @Scheduled(cron = "21 30 1 * * *")
    public void generateShift2Report() {
        reportExportService.generateMissingReports(LocalDateTime.now());
    }
    
    @Scheduled(cron = "21 30 8 * * *")
    public void generateShift3Report() {
        reportExportService.generateMissingReports(LocalDateTime.now());
    }
}
