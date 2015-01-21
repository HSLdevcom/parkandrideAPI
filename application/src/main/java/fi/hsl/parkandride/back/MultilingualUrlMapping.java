package fi.hsl.parkandride.back;

import com.mysema.query.types.path.StringPath;

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
