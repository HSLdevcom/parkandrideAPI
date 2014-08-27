package fi.hsl.parkandride.adapter.rest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingArea {
    private Long parkingAreaId;
    private MultiLingualString parkingAreaName;

    public String getParkingAreaId() {
        return String.valueOf(parkingAreaId);
    }

    public void setParkingAreaId(Long parkingAreaId) {
        this.parkingAreaId = parkingAreaId;
    }

    public MultiLingualString getParkingAreaName() {
        return parkingAreaName;
    }

    public void setParkingAreaName(MultiLingualString parkingAreaName) {
        this.parkingAreaName = parkingAreaName;
    }

    public fi.hsl.parkandride.core.domain.ParkingArea toCoreDomain() {
        fi.hsl.parkandride.core.domain.ParkingArea to = new fi.hsl.parkandride.core.domain.ParkingArea();
        to.setId(parkingAreaId);
        to.setName(parkingAreaName.toCoreDomain());
        return to;
    }

    public static ParkingArea fromCoreDomain(fi.hsl.parkandride.core.domain.ParkingArea from) {
        ParkingArea to = new ParkingArea();
        to.setParkingAreaId(from.getId());
        to.setParkingAreaName(MultiLingualString.fromCoreDomain(from.getName()));
        return to;
    }
}
