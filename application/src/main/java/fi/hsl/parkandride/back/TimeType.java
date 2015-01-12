package fi.hsl.parkandride.back;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.annotation.Nullable;

import com.mysema.query.sql.types.AbstractType;

import fi.hsl.parkandride.core.domain.Time;

public class TimeType extends AbstractType<Time> {

    public TimeType() {
        super(Types.SMALLINT);
    }

    @Override
    public Class<Time> getReturnedClass() {
        return Time.class;
    }

    @Nullable
    @Override
    public Time getValue(ResultSet rs, int startIndex) throws SQLException {
        int time = rs.getShort(startIndex);
        if (!rs.wasNull()) {
            return new Time(time);
        }
        return null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Time value) throws SQLException {
        if (value == null) {
            st.setNull(startIndex, Types.SMALLINT);
        } else {
            st.setShort(startIndex, (short) value.getMinuteOfDay());
        }
    }

}
