package fi.hsl.parkandride.outbound.sql;

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
 * QHubFacility is a Querydsl query type for QHubFacility
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHubFacility extends RelationalPathSpatial<QHubFacility> {

    private static final long serialVersionUID = -1010863708;

    public static final QHubFacility hubFacility = new QHubFacility("HUB_FACILITY");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Long> hubId = createNumber("hubId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QHubFacility> constraintE = createPrimaryKey(facilityId, hubId);

    public final com.mysema.query.sql.ForeignKey<QHub> hubFacilityHubIdFk = createForeignKey(hubId, "ID");

    public QHubFacility(String variable) {
        super(QHubFacility.class, forVariable(variable), "PUBLIC", "HUB_FACILITY");
        addMetadata();
    }

    public QHubFacility(String variable, String schema, String table) {
        super(QHubFacility.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHubFacility(Path<? extends QHubFacility> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "HUB_FACILITY");
        addMetadata();
    }

    public QHubFacility(PathMetadata<?> metadata) {
        super(QHubFacility.class, metadata, "PUBLIC", "HUB_FACILITY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(hubId, ColumnMetadata.named("HUB_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

