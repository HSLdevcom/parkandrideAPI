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
 * QFacilityAlias is a Querydsl query type for QFacilityAlias
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityAlias extends RelationalPathSpatial<QFacilityAlias> {

    private static final long serialVersionUID = -1014035070;

    public static final QFacilityAlias facilityAlias = new QFacilityAlias("FACILITY_ALIAS");

    public final StringPath alias = createString("alias");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QFacilityAlias> constraintB = createPrimaryKey(alias, facilityId);

    public final com.mysema.query.sql.ForeignKey<QFacility> facilityAliasFk = createForeignKey(facilityId, "ID");

    public QFacilityAlias(String variable) {
        super(QFacilityAlias.class, forVariable(variable), "PUBLIC", "FACILITY_ALIAS");
        addMetadata();
    }

    public QFacilityAlias(String variable, String schema, String table) {
        super(QFacilityAlias.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityAlias(Path<? extends QFacilityAlias> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_ALIAS");
        addMetadata();
    }

    public QFacilityAlias(PathMetadata<?> metadata) {
        super(QFacilityAlias.class, metadata, "PUBLIC", "FACILITY_ALIAS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(alias, ColumnMetadata.named("ALIAS").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

