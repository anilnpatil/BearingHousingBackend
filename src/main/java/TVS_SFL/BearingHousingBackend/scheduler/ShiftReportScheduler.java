package TVS_SFL.BearingHousingBackend.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
     * Shift 1: 06:00 to 14:00
     * Shift 2: 14:00 to 22:00
     * Shift 3: 22:00 to 06:00
     */    
    
    @Scheduled(cron = "0 0 14 * * *")
    public void generateShift1Report() {
        reportExportService.generateMissingReports(LocalDateTime.now());
    }

    
    @Scheduled(cron = "0 0 22 * * *")
    public void generateShift2Report() {
        reportExportService.generateMissingReports(LocalDateTime.now());
    }
    
    @Scheduled(cron = "0 0 6 * * *")
    public void generateShift3Report() {
        reportExportService.generateMissingReports(LocalDateTime.now());
    }
}
