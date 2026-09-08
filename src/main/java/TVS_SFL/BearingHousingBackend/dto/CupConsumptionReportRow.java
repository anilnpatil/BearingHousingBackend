package TVS_SFL.BearingHousingBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CupConsumptionReportRow {

    private String period;

    private Integer shift;

    private Long cupConsumed;
}
