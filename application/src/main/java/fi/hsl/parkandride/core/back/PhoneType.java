package fi.hsl.parkandride.core.back;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.annotation.Nullable;

import com.mysema.query.sql.types.AbstractType;

import fi.hsl.parkandride.core.domain.Phone;

public class PhoneType extends AbstractType<Phone> {

    public PhoneType() {
        super(Types.VARCHAR);
    }

    @Override
    public Class<Phone> getReturnedClass() {
        return Phone.class;
    }

    @Nullable
    @Override
    public Phone getValue(ResultSet rs, int startIndex) throws SQLException {
        String number = rs.getString(startIndex);
        return number != null ? new Phone(number) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Phone value) throws SQLException {
        if (value == null) {
            st.setNull(startIndex, Types.VARCHAR);
        } else {
            st.setString(startIndex, value.toString());
        }
    }
}
