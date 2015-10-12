// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package com.querydsl.sql.spatial;

import com.querydsl.sql.types.AbstractType;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PGGeometryType<T extends Geometry> extends AbstractType<T> {

    private final Class<T> type;

    public PGGeometryType(Class<T> type) {
        super(Types.STRUCT);
        this.type = type;
    }

    @Override
    public Class<T> getReturnedClass() {
        return type;
    }

    @Override
    @Nullable
    public T getValue(ResultSet rs, int startIndex) throws SQLException {
        Object obj = rs.getObject(startIndex);
        if (!(obj instanceof PGgeometry)) {
            obj = new PGgeometry(((PGobject) obj).getValue());
        }
        return obj != null ? (T) PGgeometryConverter.convert(((PGgeometry) obj).getGeometry()) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, T value) throws SQLException {
        PGgeometry geometry = new PGgeometry(PGgeometryConverter.convert(value));
        st.setObject(startIndex, geometry);
    }

    @Override
    public String getLiteral(T geometry) {
        String str = Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(geometry);
        return "'" + str + "'";
    }

}