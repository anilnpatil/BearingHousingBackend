package TVS_SFL.BearingHousingBackend.dto;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BearingHousingDataToImageResponse {

    private BearingHousingProductionData productionData;

    private BearingHousingImageInfo p1_beforeImage;
    private BearingHousingImageInfo p1_afterImage;
    private BearingHousingImageInfo p1_graphImage;

    private BearingHousingImageInfo p2_beforeImage;
    private BearingHousingImageInfo p2_afterImage;
    private BearingHousingImageInfo p2_graphImage;

}