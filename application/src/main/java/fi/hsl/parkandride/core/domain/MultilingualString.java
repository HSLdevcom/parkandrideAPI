package fi.hsl.parkandride.core.domain;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class MultilingualString {

    public static Comparator<MultilingualString> COMPARATOR =
            comparing(((MultilingualString s) -> s.fi), nullsLast(naturalOrder()))
            .thenComparing(((MultilingualString s) -> s.sv), nullsLast(naturalOrder()))
            .thenComparing(((MultilingualString s) -> s.en), nullsLast(naturalOrder()));

    @ApiModelProperty(value="Value in Finnish", required = true)
    @NotBlank
    @Length(min=0, max=255)
    public String fi;

    @ApiModelProperty(value="Value in Swedish", required = true)
    @NotBlank
    @Length(min=0, max=255)
    public String sv;

    @ApiModelProperty(value="Value in English", required = true)
    @NotBlank
    @Length(min=0, max=255)
    public String en;

    public MultilingualString() {}

    public MultilingualString(String all) {
        this(all, all, all);
    }
    public MultilingualString(String fi, String sv, String en) {
        this.fi = fi;
        this.sv = sv;
        this.en = en;
    }

    public String getFi() {
        return fi;
    }

    public String getSv() {
        return sv;
    }

    public String getEn() {
        return en;
    }

    @Override
    public int hashCode() {
        int hashCode = fi==null ? 1 : fi.hashCode();
        hashCode = 31*hashCode + (sv==null ? 0 : sv.hashCode());
        return 31*hashCode + (en==null ? 0 : en.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof MultilingualString) {
            MultilingualString other = (MultilingualString) obj;
            return Objects.equals(this.fi, other.fi) && Objects.equals(this.sv, other.sv) && Objects.equals(this.en, other.en);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(MultilingualString.class)
                .add("fi", fi)
                .add("sv", sv)
                .add("en", en)
                .toString();
    }

    public Map<String, String> asMap() {
        return new AbstractMap<String, String>() {
            @Override
            public Set<Entry<String, String>> entrySet() {
                return ImmutableSet.of(
                        new SimpleEntry<>("fi", fi),
                        new SimpleEntry<>("sv", sv),
                        new SimpleEntry<>("en", en)
                );
            }

            @Override
            public String put(String key, String value) {
                String oldValue;
                switch (key) {
                    case "fi": oldValue = fi; fi = value; break;
                    case "sv": oldValue = sv; sv = value; break;
                    case "en": oldValue = en; en = value; break;
                    default: throw new IllegalArgumentException("Unsupported lang: " + key);
                }
                return oldValue;
            }
        };
    }

}
