package fi.hsl.parkandride.back.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;

import com.mysema.query.sql.spatial.RelationalPathSpatial;

import com.mysema.query.spatial.path.*;



/**
 * QPort is a Querydsl query type for QPort
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPort extends RelationalPathSpatial<QPort> {

    private static final long serialVersionUID = 1614288076;

    public static final QPort port = new QPort("PORT");

    public final BooleanPath bicycle = createBoolean("bicycle");

    public final StringPath cityEn = createString("cityEn");

    public final StringPath cityFi = createString("cityFi");

    public final StringPath citySv = createString("citySv");

    public final BooleanPath entry = createBoolean("entry");

    public final BooleanPath exit = createBoolean("exit");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final StringPath infoEn = createString("infoEn");

    public final StringPath infoFi = createString("infoFi");

    public final StringPath infoSv = createString("infoSv");

    public final GeometryPath<org.geolatte.geom.Geometry> location = createGeometry("location", org.geolatte.geom.Geometry.class);

    public final BooleanPath pedestrian = createBoolean("pedestrian");

    public final NumberPath<Integer> portIndex = createNumber("portIndex", Integer.class);

    public final StringPath postalCode = createString("postalCode");

    public final StringPath streetAddressEn = createString("streetAddressEn");

    public final StringPath streetAddressFi = createString("streetAddressFi");

    public final StringPath streetAddressSv = createString("streetAddressSv");

    public final com.mysema.query.sql.PrimaryKey<QPort> constraint25 = createPrimaryKey(facilityId, portIndex);

    public final com.mysema.query.sql.ForeignKey<QFacility> portFacilityIdFk = createForeignKey(facilityId, "ID");

    public QPort(String variable) {
        super(QPort.class, forVariable(variable), "PUBLIC", "PORT");
        addMetadata();
    }

    public QPort(String variable, String schema, String table) {
        super(QPort.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPort(Path<? extends QPort> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PORT");
        addMetadata();
    }

    public QPort(PathMetadata<?> metadata) {
        super(QPort.class, metadata, "PUBLIC", "PORT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(bicycle, ColumnMetadata.named("BICYCLE").withIndex(6).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(cityEn, ColumnMetadata.named("CITY_EN").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(cityFi, ColumnMetadata.named("CITY_FI").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(citySv, ColumnMetadata.named("CITY_SV").withIndex(13).ofType(Types.VARCHAR).withSize(255));
        addMetadata(entry, ColumnMetadata.named("ENTRY").withIndex(3).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(exit, ColumnMetadata.named("EXIT").withIndex(4).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(infoEn, ColumnMetadata.named("INFO_EN").withIndex(17).ofType(Types.VARCHAR).withSize(255));
        addMetadata(infoFi, ColumnMetadata.named("INFO_FI").withIndex(15).ofType(Types.VARCHAR).withSize(255));
        addMetadata(infoSv, ColumnMetadata.named("INFO_SV").withIndex(16).ofType(Types.VARCHAR).withSize(255));
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(7).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(pedestrian, ColumnMetadata.named("PEDESTRIAN").withIndex(5).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(portIndex, ColumnMetadata.named("PORT_INDEX").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(postalCode, ColumnMetadata.named("POSTAL_CODE").withIndex(11).ofType(Types.VARCHAR).withSize(5));
        addMetadata(streetAddressEn, ColumnMetadata.named("STREET_ADDRESS_EN").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(streetAddressFi, ColumnMetadata.named("STREET_ADDRESS_FI").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(streetAddressSv, ColumnMetadata.named("STREET_ADDRESS_SV").withIndex(9).ofType(Types.VARCHAR).withSize(255));
    }

}

