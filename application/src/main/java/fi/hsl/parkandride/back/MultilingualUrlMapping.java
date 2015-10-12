// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.core.types.dsl.StringPath;

import fi.hsl.parkandride.core.domain.MultilingualUrl;

public class MultilingualUrlMapping extends AbstractMultilingualStringMapping<MultilingualUrl> {

    public MultilingualUrlMapping(StringPath fiPath, StringPath svPath, StringPath enPath) {
        super(MultilingualUrl.class, fiPath, svPath, enPath);

    }

    @Override
    protected MultilingualUrl newMultilingualString(String fi, String sv, String en) {
        return new MultilingualUrl(fi, sv, en);
    }

}
