package fi.hsl.parkandride.front;

public class UrlSchema {

    public static final String GEOJSON = "application/vnd.geo+json";

    public static final String API_KEY = "apiKey";

    public static final String DOCS = "/docs";


    public static final String API = "/api/v1";

    public static final String FACILITIES = API + "/facilities";

    public static final String FACILITY_ID = "facilityId";

    public static final String FACILITY = FACILITIES + "/{" + FACILITY_ID + "}" ;

    public static final String FACILITY_STATUS = FACILITY + "/status" ;

    public static final String CAPACITY_TYPES = API + "/capacity-types";

    public static final String USAGES = API + "/usages";

    public static final String DAY_TYPES = API + "/day-types";


    public static final String HUBS = API + "/hubs";

    public static final String HUB_ID = "hubId";

    public static final String HUB = HUBS + "/{" + HUB_ID + "}" ;


    public static final String CONTACTS = API + "/contacts";

    public static final String CONTACT_ID = "contactId";

    public static final String CONTACT = CONTACTS + "/{" + CONTACT_ID + "}" ;


    public static final String SERVICES = API + "/services";


    public static final String OPERATORS = API + "/operators";

    public static final String OPERATOR_ID = "operatorId";

    public static final String OPERATOR = OPERATORS + "/{" + OPERATOR_ID + "}" ;



    public static final String INTERNAL = "/internal";

    public static final String FEATURES = INTERNAL + "/features" ;

    public static final String LOGIN = INTERNAL + "/login";


    public static final String PAYMENT_METHODS = API + "/payment-methods";

    /**
     * TESTING
     */
    public static final String DEV_API = "/dev-api";

    public static final String DEV_OPERATORS = DEV_API + "/operators";

    public static final String DEV_USERS = DEV_API + "/users";

    public static final String DEV_CONTACTS = DEV_API + "/contacts";

    public static final String DEV_FACILITIES = DEV_API + "/facilities";

    public static final String DEV_LOGIN = DEV_API + "/login";

    public static final String DEV_HUBS = DEV_API + "/hubs";

}
