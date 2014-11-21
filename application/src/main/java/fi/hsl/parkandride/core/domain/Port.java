package fi.hsl.parkandride.core.domain;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;

import com.google.common.base.MoreObjects;

public class Port {

    @NotNull
    public Geometry location;

    public boolean entry;

    public boolean exit;

    public boolean pedestrian;

    @Valid
    public Address address;

    @Valid
    public MultilingualString info;

    public Port() {}

    public Port(Geometry location, boolean entry, boolean exit, boolean pedestrian) {
        this(location, entry, exit, pedestrian, null, null, null, null);
    }

    public Port(Geometry location, boolean entry, boolean exit, boolean pedestrian, String streetAddress, String postalCode, String city, String info) {
        this.location = location;
        this.entry = entry;
        this.exit = exit;
        this.pedestrian = pedestrian;
        this.address = new Address(streetAddress, postalCode, city);
        this.info = info != null ? new MultilingualString(info, info, info) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Port) {
            Port other = (Port) obj;
            return this.entry == other.entry
                    && this.exit == other.exit
                    && this.pedestrian == other.pedestrian
                    && this.location.equals(other.location)
                    && Objects.equals(this.address, other.address)
                    && Objects.equals(this.info, other.info);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = Boolean.hashCode(entry);
        hashCode = 31*hashCode + Boolean.hashCode(exit);
        hashCode = 31*hashCode + Boolean.hashCode(pedestrian);
        hashCode = 31*hashCode + (address == null ? 0 : address.hashCode());
        hashCode = 31*hashCode + (info == null ? 0 : info.hashCode());
        return 31*hashCode + location.hashCode();
    }

    public boolean isEntry() {
        return entry;
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isPedestrian() {
        return pedestrian;
    }

    public Geometry getLocation() {
        return location;
    }

    public Address getAddress() {
        return address;
    }

    public String toString() {
        return MoreObjects.toStringHelper(Port.class)
                .add("location", location)
                .add("entry", entry)
                .add("exit", exit)
                .add("pedestrian", pedestrian)
                .add("address", address)
                .add("info", info)
                .toString();
    }
}
