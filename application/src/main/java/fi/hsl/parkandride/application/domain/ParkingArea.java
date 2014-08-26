package fi.hsl.parkandride.application.domain;

import java.util.Collections;
import java.util.List;

public class ParkingArea implements IdentifiedDomainObject {
    private Long id;
    private MultiLingualString name;
    private ParkingProperties properties;
    private List<ParkingFacility> facilities = Collections.emptyList();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public MultiLingualString getName() {
        return name;
    }

    public void setName(MultiLingualString name) {
        this.name = name;
    }

    public ParkingProperties getProperties() {
        return properties;
    }

    public void setProperties(ParkingProperties properties) {
        this.properties = properties;
    }

    public void setFacilities(List<ParkingFacility> facilities) {
        this.facilities = Collections.unmodifiableList(facilities);
    }

    public List<ParkingFacility> getFacilities() {
        return this.facilities;
    }
}
