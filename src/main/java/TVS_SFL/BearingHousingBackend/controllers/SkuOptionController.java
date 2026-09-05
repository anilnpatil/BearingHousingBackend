package TVS_SFL.BearingHousingBackend.controllers;

import TVS_SFL.BearingHousingBackend.dto.SkuOption;
import TVS_SFL.BearingHousingBackend.services.SkuOptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sku-options")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SkuOptionController {
    private final SkuOptionService skuOptionService;

    public SkuOptionController(SkuOptionService skuOptionService) {
        this.skuOptionService = skuOptionService;
    }

    @GetMapping
    public List<SkuOption> getAll() {
        return skuOptionService.getAll();
    }

    @PostMapping
    public ResponseEntity<SkuOption> add(@RequestBody SkuOptionRequest request) {
        return ResponseEntity.status(201).body(skuOptionService.add(request.value()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skuOptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record SkuOptionRequest(String value) {
    }
}