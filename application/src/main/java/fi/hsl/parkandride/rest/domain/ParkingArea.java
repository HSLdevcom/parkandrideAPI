package fi.hsl.parkandride.rest.domain;

import fi.hsl.parkandride.application.domain.MultiLingualString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingArea {
    private Long parkingAreaId;
    private MultiLingualString name;

    public String getParkingAreaId() {
        return String.valueOf(parkingAreaId);
    }

    public void setParkingAreaId(Long parkingAreaId) {
        this.parkingAreaId = parkingAreaId;
    }

    public MultiLingualString getName() {
        return name;
    }

    public void setName(MultiLingualString name) {
        this.name = name;
    }

    public fi.hsl.parkandride.application.domain.ParkingArea toApplicationDomain() {
        fi.hsl.parkandride.application.domain.ParkingArea applicationDomain = new fi.hsl.parkandride.application.domain.ParkingArea();
        applicationDomain.setId(parkingAreaId);
        applicationDomain.setName(getName());
        return applicationDomain;
    }

    public static ParkingArea fromApplicationDomain(fi.hsl.parkandride.application.domain.ParkingArea applicationDomain) {
        ParkingArea restDomain = new ParkingArea();
        restDomain.setParkingAreaId(applicationDomain.getId());
        restDomain.setName(applicationDomain.getName());
        return restDomain;
    }
}
