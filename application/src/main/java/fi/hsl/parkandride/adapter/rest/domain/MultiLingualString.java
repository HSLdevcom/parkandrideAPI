package fi.hsl.parkandride.adapter.rest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MultiLingualString {
    @JsonProperty("fi-FI")
    public String fi;

    @JsonProperty("se-SE")
    public String sv;

    @JsonProperty("en-EN")
    public String  en;

    public fi.hsl.parkandride.core.domain.MultiLingualString toCoreDomain() {
        return new fi.hsl.parkandride.core.domain.MultiLingualString(fi, sv, en);
    }

    public static MultiLingualString fromCoreDomain(fi.hsl.parkandride.core.domain.MultiLingualString from) {
        MultiLingualString to = new MultiLingualString();
        to.fi = from.fi;
        to.sv = from.sv;
        to.en = from.en;
        return to;
    }
}
