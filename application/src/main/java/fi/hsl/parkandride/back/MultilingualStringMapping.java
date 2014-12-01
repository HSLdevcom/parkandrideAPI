package fi.hsl.parkandride.back;

import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.path.StringPath;

import fi.hsl.parkandride.core.domain.MultilingualString;

public class MultilingualStringMapping extends MappingProjection<MultilingualString> {

    private final StringPath fiPath;
    private final StringPath svPath;
    private final StringPath enPath;

    public MultilingualStringMapping(StringPath fiPath, StringPath svPath, StringPath enPath) {
        super(MultilingualString.class, fiPath, svPath, enPath);
        this.fiPath = fiPath;
        this.svPath = svPath;
        this.enPath = enPath;
    }

    @Override
    protected MultilingualString map(Tuple row) {
        String fi = row.get(fiPath);
        String sv = row.get(svPath);
        String en = row.get(enPath);
        if (fi != null || sv != null || en != null) {
            return new MultilingualString(fi, sv, en);
        }
        return null;
    }

    protected void populate(MultilingualString string, StoreClause<?> store) {
        if (string != null) {
            store.set(fiPath, string.fi)
                    .set(svPath, string.sv)
                    .set(enPath, string.en);
        } else {
            store.setNull(fiPath).setNull(svPath).setNull(enPath);
        }
    }
}
