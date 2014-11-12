package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;

import com.google.common.base.MoreObjects;

public class Port {

    @NotNull
    public Geometry location;

    public boolean entry;

    public boolean exit;

    public boolean pedestrian;

    public Port() {}

    public Port(Geometry location, boolean entry, boolean exit, boolean pedestrian) {
        this.location = location;
        this.entry = entry;
        this.exit = exit;
        this.pedestrian = pedestrian;
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
                    && this.location.equals(other.location);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = Boolean.hashCode(entry);
        hashCode = 31*hashCode + Boolean.hashCode(exit);
        hashCode = 31*hashCode + Boolean.hashCode(pedestrian);
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

    public String toString() {
        return MoreObjects.toStringHelper(Port.class)
                .add("location", location)
                .add("entry", entry)
                .add("exit", exit)
                .add("pedestrian", pedestrian)
                .toString();
    }
}
