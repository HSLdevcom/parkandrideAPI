package fi.hsl.parkandride.back;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;

import com.mysema.query.sql.types.AbstractType;

// FIXME: Replace Querydsl once https://github.com/querydsl/querydsl/issues/943 is released
public class H2GeometryWktType extends AbstractType<Geometry> {

    public static final H2GeometryWktType DEFAULT = new H2GeometryWktType();

    public H2GeometryWktType() {
        super(Types.VARCHAR);
    }

    @Override
    public Class<Geometry> getReturnedClass() {
        return Geometry.class;
    }

    @Override
    public Geometry getValue(ResultSet rs, int startIndex) throws SQLException {
        String str = rs.getString(startIndex);
        if (str != null) {
            return Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(str);
        } else {
            return null;
        }
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Geometry value) throws SQLException {
        String str = Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(value);
        if (str.startsWith("SRID=")) {
            str = str.substring(str.indexOf(';') + 1);
        }
        st.setString(startIndex, str);
    }

    @Override
    public String getLiteral(Geometry geometry) {
        String str = Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(geometry);
        return "'" + str + "'";
    }
}
