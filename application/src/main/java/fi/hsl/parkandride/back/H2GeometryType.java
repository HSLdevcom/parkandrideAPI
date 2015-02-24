package fi.hsl.parkandride.back;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.annotation.Nullable;

import org.geolatte.geom.ByteBuffer;
import org.geolatte.geom.ByteOrder;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkb;
import org.geolatte.geom.codec.WkbDecoder;
import org.geolatte.geom.codec.WkbEncoder;
import org.geolatte.geom.codec.Wkt;

import com.mysema.query.sql.types.AbstractType;

public class H2GeometryType<T extends Geometry> extends AbstractType<T> {

    private final ByteOrder byteOrder = ByteOrder.NDR;

    private final Class<T> type;

    public H2GeometryType(Class<T> type) {
        super(Types.BLOB);
        this.type = type;
    }

    @Override
    public Class<T> getReturnedClass() {
        return type;
    }

    @Override
    @Nullable
    public T getValue(ResultSet rs, int startIndex) throws SQLException {
        byte[] bytes = rs.getBytes(startIndex);
        if (bytes != null) {
            byte[] wkb;
            if (bytes[0] != 0 && bytes[0] != 1) { // decodes EWKB
                wkb = new byte[bytes.length - 32];
                System.arraycopy(bytes, 32, wkb, 0, wkb.length);
            } else {
                wkb = bytes;
            }
            WkbDecoder decoder = Wkb.newDecoder(Wkb.Dialect.POSTGIS_EWKB_1);
            return (T) decoder.decode(ByteBuffer.from(wkb));
        } else {
            return null;
        }
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, T value) throws SQLException {
        WkbEncoder encoder = Wkb.newEncoder(Wkb.Dialect.POSTGIS_EWKB_1);
        ByteBuffer buffer = encoder.encode(value, byteOrder);
        st.setBytes(startIndex, buffer.toByteArray());
    }

    @Override
    public String getLiteral(T geometry) {
        String str = Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(geometry);
        if (geometry.getSRID() > -1) {
            return "ST_GeomFromText('" + str + "', " + geometry.getSRID() + ")";
        } else {
            return "ST_GeomFromText('" + str + "')";
        }
    }

}
