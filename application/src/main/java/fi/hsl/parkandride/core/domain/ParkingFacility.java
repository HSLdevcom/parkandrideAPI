package fi.hsl.parkandride.core.domain;

public class ParkingFacility implements IdentifiedDomainObject {
    private Long id;
    private MultiLingualString name;
    private ParkingProperties properties;

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
}
