package TVS_SFL.BearingHousingBackend.dto;

import TVS_SFL.BearingHousingBackend.entities.BearingHousingProductionData;

public class DataLocationResult {

    private DataLocation location;
    private BearingHousingProductionData data;

    public DataLocationResult() {
    }

    public DataLocationResult(DataLocation location, BearingHousingProductionData data) {
        this.location = location;
        this.data = data;
    }

    public DataLocation getLocation() {
        return location;
    }

    public void setLocation(DataLocation location) {
        this.location = location;
    }

    public BearingHousingProductionData getData() {
        return data;
    }

    public void setData(BearingHousingProductionData data) {
        this.data = data;
    }
}
