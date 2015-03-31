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
 * QUsage is a Querydsl query type for QUsage
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUsage extends RelationalPathSpatial<QUsage> {

    private static final long serialVersionUID = -1491957066;

    public static final QUsage usage = new QUsage("USAGE");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QUsage> constraint4 = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QFacilityPrediction> _facilityPredictionUsageFk = createInvForeignKey(name, "USAGE");

    public final com.mysema.query.sql.ForeignKey<QPricing> _pricingUsageFk = createInvForeignKey(name, "USAGE");

    public final com.mysema.query.sql.ForeignKey<QFacilityUtilization> _facilityUtilizationUsageFk = createInvForeignKey(name, "USAGE");

    public final com.mysema.query.sql.ForeignKey<QUnavailableCapacity> _unavailableCapacityUsageFk = createInvForeignKey(name, "USAGE");

    public final com.mysema.query.sql.ForeignKey<QPredictor> _predictorUsageFk = createInvForeignKey(name, "USAGE");

    public QUsage(String variable) {
        super(QUsage.class, forVariable(variable), "PUBLIC", "USAGE");
        addMetadata();
    }

    public QUsage(String variable, String schema, String table) {
        super(QUsage.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUsage(Path<? extends QUsage> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "USAGE");
        addMetadata();
    }

    public QUsage(PathMetadata<?> metadata) {
        super(QUsage.class, metadata, "PUBLIC", "USAGE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

