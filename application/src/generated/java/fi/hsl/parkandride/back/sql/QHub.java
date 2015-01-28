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
 * QHub is a Querydsl query type for QHub
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHub extends RelationalPathSpatial<QHub> {

    private static final long serialVersionUID = -1194859702;

    public static final QHub hub = new QHub("HUB");

    public final StringPath cityEn = createString("cityEn");

    public final StringPath cityFi = createString("cityFi");

    public final StringPath citySv = createString("citySv");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final PointPath<org.geolatte.geom.Point> location = createPoint("location", org.geolatte.geom.Point.class);

    public final StringPath nameEn = createString("nameEn");

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final StringPath postalCode = createString("postalCode");

    public final StringPath streetAddressEn = createString("streetAddressEn");

    public final StringPath streetAddressFi = createString("streetAddressFi");

    public final StringPath streetAddressSv = createString("streetAddressSv");

    public final com.mysema.query.sql.PrimaryKey<QHub> constraint118 = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QHubFacility> _hubFacilityHubIdFk = createInvForeignKey(id, "HUB_ID");

    public QHub(String variable) {
        super(QHub.class, forVariable(variable), "PUBLIC", "HUB");
        addMetadata();
    }

    public QHub(String variable, String schema, String table) {
        super(QHub.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHub(Path<? extends QHub> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "HUB");
        addMetadata();
    }

    public QHub(PathMetadata<?> metadata) {
        super(QHub.class, metadata, "PUBLIC", "HUB");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(cityEn, ColumnMetadata.named("CITY_EN").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(cityFi, ColumnMetadata.named("CITY_FI").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(citySv, ColumnMetadata.named("CITY_SV").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(location, ColumnMetadata.named("LOCATION").withIndex(5).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(postalCode, ColumnMetadata.named("POSTAL_CODE").withIndex(9).ofType(Types.VARCHAR).withSize(5));
        addMetadata(streetAddressEn, ColumnMetadata.named("STREET_ADDRESS_EN").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(streetAddressFi, ColumnMetadata.named("STREET_ADDRESS_FI").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(streetAddressSv, ColumnMetadata.named("STREET_ADDRESS_SV").withIndex(7).ofType(Types.VARCHAR).withSize(255));
    }

}

