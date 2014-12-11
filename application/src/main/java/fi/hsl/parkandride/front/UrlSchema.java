package fi.hsl.parkandride.front;

public class UrlSchema {

    public static final String GEOJSON = "application/vnd.geo+json";


    public static final String API = "/api/v1";

    public static final String FACILITIES = API + "/facilities";

    public static final String FACILITY_ID = "facilityId";

    public static final String FACILITY = FACILITIES + "/{" + FACILITY_ID + "}" ;

    public static final String FACILITY_STATUS = FACILITY + "/status" ;

    public static final String CAPACITY_TYPES = API + "/capacity-types";


    public static final String HUBS = API + "/hubs";

    public static final String HUB_ID = "hubId";

    public static final String HUB = HUBS + "/{" + HUB_ID + "}" ;


    public static final String CONTACTS = API + "/contacts";

    public static final String CONTACT_ID = "contactId";

    public static final String CONTACT = CONTACTS + "/{" + CONTACT_ID + "}" ;


    public static final String SERVICES = API + "/services";

    public static final String SERVICE_ID = "serviceId";

    public static final String SERVICE = SERVICES + "/{" + SERVICE_ID + "}" ;


    public static final String OPERATORS = API + "/operators";

    public static final String OPERATOR_ID = "operatorId";

    public static final String OPERATOR = OPERATORS + "/{" + OPERATOR_ID + "}" ;


    public static final String FEATURES = API + "/features" ;


    public static final String INTERNAL = "/internal";

    public static final String LOGIN = INTERNAL + "/login";

    /**
     * TESTING
     */
    public static final String DEV_API = "/dev-api";

    public static final String DEV_CONTACTS = DEV_API + "/contacts";

    public static final String DEV_FACILITIES = DEV_API + "/facilities";

    public static final String DEV_HUBS = DEV_API + "/hubs";

}
