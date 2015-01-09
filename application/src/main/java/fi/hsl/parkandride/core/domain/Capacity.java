package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.Min;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class Capacity {

    @ApiModelProperty(required = true, allowableValues = "range[1, infinity]")
    @Min(1)
    public int built;

    @ApiModelProperty(required = true, allowableValues = "range[0, infinity]")
    @Min(0)
    public int unavailable;

    public Capacity() {}

    public Capacity(int built) {
        this(built, 0);
    }

    public Capacity(int built, int unavailable) {
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

    @Override
    public String toString() {
        return "" + built +  " -" + unavailable;
    }


    public int getBuilt() {
        return built;
    }

    public int getUnavailable() {
        return unavailable;
    }
}
