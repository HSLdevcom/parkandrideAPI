package fi.hsl.parkandride.core.domain;

public class Capacity {

    public int built;

    public int unavailable;

    public Capacity() {}

    public Capacity(Integer built, Integer unavailable) {
        this.built = built;
        this.unavailable = unavailable;
    }

    @Override
    public int hashCode() {
        return 31*built + unavailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Capacity) {
            Capacity other = (Capacity) obj;
            return this.built == other.built && this.unavailable == other.unavailable;
        } else {
            return false;
        }
    }
}
