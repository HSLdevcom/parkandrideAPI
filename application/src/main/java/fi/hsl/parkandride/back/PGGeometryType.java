package fi.hsl.parkandride.back;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.annotation.Nullable;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.postgis.PGgeometry;

import com.mysema.query.sql.spatial.PGgeometryConverter;
import com.mysema.query.sql.types.AbstractType;

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