package TVS_SFL.BearingHousingBackend.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import TVS_SFL.BearingHousingBackend.services.ShiftReportExportService;

@Component
public class ShiftReportScheduler {

    private final ShiftReportExportService reportExportService;

    public ShiftReportScheduler(ShiftReportExportService reportExportService) {
        this.reportExportService = reportExportService;
    }

    /**
     * Runs at the end of each shift.
     * Shift 1: 06:00 to 14:00
     * Shift 2: 14:00 to 22:00
     * Shift 3: 22:00 to 06:00
     */
    
    
    @Scheduled(cron = "0 09 14 * * *")
    public void generateShift1Report() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(today, LocalTime.of(6, 0));
        LocalDateTime end = LocalDateTime.of(today, LocalTime.of(14, 0));
        reportExportService.generateShiftReport(1, start, end);
    }

    
    @Scheduled(cron = "0 09 22 * * *")
    public void generateShift2Report() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(today, LocalTime.of(14, 0));
        LocalDateTime end = LocalDateTime.of(today, LocalTime.of(22, 0));
        reportExportService.generateShiftReport(2, start, end);
    }

    
    @Scheduled(cron = "0 09 6 * * *")
    public void generateShift3Report() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(today, LocalTime.of(22, 0));
        LocalDateTime end = LocalDateTime.of(today.plusDays(1), LocalTime.of(6, 0));
        reportExportService.generateShiftReport(3, start, end);
    }
}
