package TVS_SFL.BearingHousingBackend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import TVS_SFL.BearingHousingBackend.dto.CupConsumptionReportRow;
import TVS_SFL.BearingHousingBackend.services.CupConsumptionReportService;

@RestController
@RequestMapping("/api/reports/cupConsumption")
@RequiredArgsConstructor
public class CupConsumptionReportController {

    private final CupConsumptionReportService service;

    @GetMapping("/day")
    public ResponseEntity<List<CupConsumptionReportRow>> getDayReport(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) Integer shift) {
        return ResponseEntity.ok(service.getDayReport(from, to, shift));
    }

    @GetMapping("/month")
    public ResponseEntity<List<CupConsumptionReportRow>> getMonthReport(
            @RequestParam int year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer shift) {
        return ResponseEntity.ok(service.getMonthReport(year, month, shift));
    }

    @GetMapping("/year")
    public ResponseEntity<List<CupConsumptionReportRow>> getYearReport(
            @RequestParam int year,
            @RequestParam(required = false) Integer shift) {
        return ResponseEntity.ok(service.getYearReport(year, shift));
    }
}
