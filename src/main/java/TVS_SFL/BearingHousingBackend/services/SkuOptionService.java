package TVS_SFL.BearingHousingBackend.services;

import TVS_SFL.BearingHousingBackend.dto.SkuOption;
import java.util.List;

public interface SkuOptionService {
    List<SkuOption> getAll();

    SkuOption add(String value);

    void delete(Long id);
}