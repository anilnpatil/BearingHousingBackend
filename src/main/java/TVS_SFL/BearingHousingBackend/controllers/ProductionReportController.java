package TVS_SFL.BearingHousingBackend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import TVS_SFL.BearingHousingBackend.dto.ProductionReportRow;
import TVS_SFL.BearingHousingBackend.services.ProductionReportService;

@RestController
@RequestMapping("/api/reports/productionSummary")
@RequiredArgsConstructor
public class ProductionReportController {

    private final ProductionReportService productionReportService;

    @GetMapping("/day")
    public ResponseEntity<List<ProductionReportRow>> getDayReport(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Integer shift) {              

        List<ProductionReportRow> result =
                productionReportService.getDayReport(
                        from,
                        to,
                        sku,
                        shift
                );

        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/week")
    public ResponseEntity<List<ProductionReportRow>> getWeekReport(
            @RequestParam int year,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Integer shift) {

        List<ProductionReportRow> result =
                productionReportService.getWeekReport(
                        year,
                        sku,
                        shift
                );

        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/month")
    public ResponseEntity<List<ProductionReportRow>> getMonthReport(
            @RequestParam int year,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Integer shift) {

        List<ProductionReportRow> result =
                productionReportService.getMonthReport(
                        year,
                        sku,
                        shift
                );

        return ResponseEntity.ok(result);
    }
}