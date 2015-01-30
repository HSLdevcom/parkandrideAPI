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
 * QUtilizationStatus is a Querydsl query type for QUtilizationStatus
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUtilizationStatus extends RelationalPathSpatial<QUtilizationStatus> {

    private static final long serialVersionUID = -1829478839;

    public static final QUtilizationStatus utilizationStatus = new QUtilizationStatus("UTILIZATION_STATUS");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QUtilizationStatus> constraint11 = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QFacilityUtilization> _facilityUtilizationStatusFk = createInvForeignKey(name, "STATUS");

    public QUtilizationStatus(String variable) {
        super(QUtilizationStatus.class, forVariable(variable), "PUBLIC", "UTILIZATION_STATUS");
        addMetadata();
    }

    public QUtilizationStatus(String variable, String schema, String table) {
        super(QUtilizationStatus.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUtilizationStatus(Path<? extends QUtilizationStatus> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "UTILIZATION_STATUS");
        addMetadata();
    }

    public QUtilizationStatus(PathMetadata<?> metadata) {
        super(QUtilizationStatus.class, metadata, "PUBLIC", "UTILIZATION_STATUS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

