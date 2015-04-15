// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import org.hibernate.validator.constraints.URL;

public class MultilingualUrl extends MultilingualString {

    public MultilingualUrl() {}

    public MultilingualUrl(String all) {
        super(all);
    }

    public MultilingualUrl(String fi, String sv, String en) {
        super(fi, sv, en);
    }

    @URL(regexp = "(?i)^(?:http://|https://).*")
    @Override
    public String getFi() {
        return super.getFi();
    }

    @URL(regexp = "(?i)^(?:http://|https://).*")
    @Override
    public String getSv() {
        return super.getSv();
    }

    @URL(regexp = "(?i)^(?:http://|https://).*")
    @Override
    public String getEn() {
        return super.getEn();
    }

}
