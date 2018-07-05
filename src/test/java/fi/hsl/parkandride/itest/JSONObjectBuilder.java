// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.itest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONObjectBuilder  {
    public final JSONObject jsonObject;

    public JSONObjectBuilder() {
        jsonObject = new JSONObject();
    }

    public JSONObjectBuilder put(Object key, Object value) {
        jsonObject.put(key, value);
        return this;
    }

    public JSONObjectBuilder remove(Object key) {
        jsonObject.remove(key);
        return this;
    }

    public JSONArray asArray() {
        final JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        return jsonArray;
    }
}
