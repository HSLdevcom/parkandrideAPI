package fi.hsl.parkandride.config;

import static java.sql.Types.VARCHAR;
import static org.geolatte.geom.codec.Wkt.Dialect.POSTGIS_EWKT_1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nullable;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;

import com.mysema.query.sql.types.AbstractType;

public class H2GeometryType extends AbstractType<Geometry> {

    public static final H2GeometryType DEFAULT = new H2GeometryType();

    public H2GeometryType() {
        super(VARCHAR);
    }

    @Override
    public Class<Geometry> getReturnedClass() {
        return Geometry.class;
    }

    @Override
    @Nullable
    public Geometry getValue(ResultSet rs, int startIndex) throws SQLException {
        String wkt = rs.getString(startIndex);
        if (wkt == null) {
            return null;
        } else {
            return Wkt.newWktDecoder(POSTGIS_EWKT_1).decode(wkt);
        }
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Geometry value) throws SQLException {
        if (value == null) {
            st.setNull(startIndex, VARCHAR);
        } else {
            String wkt = Wkt.newWktEncoder(POSTGIS_EWKT_1).encode(value);
            st.setString(startIndex, wkt);
        }
    }

    @Override
    public String getLiteral(Geometry geometry) {
        String str = Wkt.newWktEncoder(POSTGIS_EWKT_1).encode(geometry);
        return "'" + str + "'";
    }

}
