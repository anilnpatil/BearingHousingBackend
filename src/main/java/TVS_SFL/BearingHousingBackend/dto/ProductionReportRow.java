package TVS_SFL.BearingHousingBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionReportRow {

    private String date;

    private String sku;

    private Integer shift;

    private Long totalCount;

    private Long okCount;

    private Long notOkCount;
}