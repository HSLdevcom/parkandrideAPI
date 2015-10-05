// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import static java.util.Arrays.asList;

public final class UrlSchema {

    private UrlSchema() {
    }

    public static final String GEOJSON = "application/vnd.geo+json";

    public static final String API_KEY = "apiKey";
    public static final String DOCS = "/docs";

    public static final String API = "/api/v1";

    public static final String FACILITIES = API + "/facilities";

    public static final String FACILITY_ID = "facilityId";
    public static final String FACILITY = FACILITIES + "/{" + FACILITY_ID + "}";
    public static final String FACILITY_UTILIZATION = FACILITY + "/utilization";
    public static final String FACILITY_PREDICTION = FACILITY + "/prediction";
    public static final String ABSOLUTE_TIME = "at";
    public static final String FACILITY_PREDICTION_ABSOLUTE = FACILITY_PREDICTION + "?" + ABSOLUTE_TIME + "={timestamp}";
    public static final String RELATIVE_TIME = "after";
    public static final String FACILITY_PREDICTION_RELATIVE = FACILITY_PREDICTION + "?" + RELATIVE_TIME + "={hhmm}";
    public static final String CAPACITY_TYPES = API + "/capacity-types";

    public static final String USAGES = API + "/usages";
    public static final String DAY_TYPES = API + "/day-types";
    public static final String HUBS = API + "/hubs";

    public static final String HUB_ID = "hubId";
    public static final String HUB = HUBS + "/{" + HUB_ID + "}";
    public static final String HUB_PREDICTION = HUB + "/prediction";
    public static final String HUB_PREDICTION_ABSOLUTE = HUB_PREDICTION + "?" + ABSOLUTE_TIME + "={timestamp}";;
    public static final String HUB_PREDICTION_RELATIVE = HUB_PREDICTION + "?" + RELATIVE_TIME + "={hhmm}";

    public static final String CONTACTS = API + "/contacts";
    public static final String CONTACT_ID = "contactId";
    public static final String CONTACT = CONTACTS + "/{" + CONTACT_ID + "}";

    public static final String SERVICES = API + "/services";

    public static final String OPERATORS = API + "/operators";
    public static final String OPERATOR_ID = "operatorId";
    public static final String OPERATOR = OPERATORS + "/{" + OPERATOR_ID + "}";

    public static final String REGIONS = API + "/regions";
    public static final String REGIONS_WITH_HUBS = API + "/regions/withHubs";

    public static final String REPORTS = API + "/reports";
    public static final String REPORT_ID = "reportId";
    public static final String REPORT = REPORTS + "/{" + REPORT_ID + "}";

    public static final String INTERNAL = "/internal";
    public static final String FEATURES = INTERNAL + "/features";
    public static final String LOGIN = INTERNAL + "/login";
    public static final String USER_ID = "userId";
    public static final String USERS = INTERNAL + "/users";
    public static final String USER = USERS + "/{" + USER_ID + "}";
    public static final String TOKEN = USERS + "/{" + USER_ID + "}/token";
    public static final String PASSWORD = USERS + "/{" + USER_ID + "}/password";
    public static final String ROLES = INTERNAL + "/roles";

    public static final String PAYMENT_METHODS = API + "/payment-methods";
    public static final String FACILITY_STATUSES = API + "/facility-statuses";
    public static final String PRICING_METHODS = API + "/pricing-methods";

    public static Collection<String> CORS_ENABLED_PATHS = asList(API + "/*", DOCS + "/*");

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
    public static final String DEV_UTILIZATION = DEV_FACILITIES + "/{" + FACILITY_ID + "}/utilization";
    public static final String DEV_PREDICTION = DEV_API + "/prediction";

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}
