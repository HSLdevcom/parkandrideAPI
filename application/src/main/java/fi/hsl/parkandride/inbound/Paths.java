package fi.hsl.parkandride.inbound;

public class Paths {

    public static final String API = "/api";

    public static final String FACILITIES = API + "/facilities";

    public static final String FACILITY_ID = "facilityId";

    public static final String FACILITY = FACILITIES + "/{" + FACILITY_ID + "}" ;

    public static final String CAPACITY_TYPES = API + "/capacity-types";
}
