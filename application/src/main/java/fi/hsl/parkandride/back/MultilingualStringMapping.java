// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.mysema.query.types.path.StringPath;

import fi.hsl.parkandride.core.domain.MultilingualString;

public class MultilingualStringMapping extends AbstractMultilingualStringMapping<MultilingualString> {

    public MultilingualStringMapping(StringPath fiPath, StringPath svPath, StringPath enPath) {
        super(MultilingualString.class, fiPath, svPath, enPath);
    }

    @Override
    protected MultilingualString newMultilingualString(String fi, String sv, String en) {
        return new MultilingualString(fi, sv, en);
    }
}
