// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.path.StringPath;

import fi.hsl.parkandride.core.domain.MultilingualString;

public abstract class AbstractMultilingualStringMapping<T extends MultilingualString> extends MappingProjection<T> {

    private final StringPath fiPath;
    private final StringPath svPath;
    private final StringPath enPath;

    public AbstractMultilingualStringMapping(Class<T> type, StringPath fiPath, StringPath svPath, StringPath enPath) {
        super(type, fiPath, svPath, enPath);
        this.fiPath = fiPath;
        this.svPath = svPath;
        this.enPath = enPath;
    }

    @Override
    protected T map(Tuple row) {
        String fi = row.get(fiPath);
        String sv = row.get(svPath);
        String en = row.get(enPath);
        if (fi != null || sv != null || en != null) {
            return newMultilingualString(fi, sv, en);
        }
        return null;
    }

    protected abstract T newMultilingualString(String fi, String sv, String en);

    protected void populate(T string, StoreClause<?> store) {
        if (string != null) {
            store.set(fiPath, string.fi)
                    .set(svPath, string.sv)
                    .set(enPath, string.en);
        } else {
            store.setNull(fiPath).setNull(svPath).setNull(enPath);
        }
    }
}
