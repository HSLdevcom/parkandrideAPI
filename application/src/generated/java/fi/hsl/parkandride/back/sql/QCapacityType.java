package fi.hsl.parkandride.back.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;

import com.querydsl.sql.spatial.RelationalPathSpatial;

import com.querydsl.spatial.*;



/**
 * QCapacityType is a Querydsl query type for QCapacityType
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCapacityType extends RelationalPathSpatial<QCapacityType> {

    private static final long serialVersionUID = 1619274847;

    public static final QCapacityType capacityType = new QCapacityType("CAPACITY_TYPE");

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> name = createEnum("name", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final com.querydsl.sql.PrimaryKey<QCapacityType> constraint54 = createPrimaryKey(name);

    public final com.querydsl.sql.ForeignKey<QPredictor> _predictorCapacityTypeFk = createInvForeignKey(name, "CAPACITY_TYPE");

    public final com.querydsl.sql.ForeignKey<QUnavailableCapacity> _unavailableCapacityCapacityTypeFk = createInvForeignKey(name, "CAPACITY_TYPE");

    public final com.querydsl.sql.ForeignKey<QFacilityPrediction> _facilityPredictionCapacityTypeFk = createInvForeignKey(name, "CAPACITY_TYPE");

    public final com.querydsl.sql.ForeignKey<QPricing> _pricingCapacityTypeFk = createInvForeignKey(name, "CAPACITY_TYPE");

    public final com.querydsl.sql.ForeignKey<QFacilityUtilization> _facilityUtilizationCapacityTypeFk = createInvForeignKey(name, "CAPACITY_TYPE");

    public QCapacityType(String variable) {
        super(QCapacityType.class, forVariable(variable), "PUBLIC", "CAPACITY_TYPE");
        addMetadata();
    }

    public QCapacityType(String variable, String schema, String table) {
        super(QCapacityType.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCapacityType(Path<? extends QCapacityType> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "CAPACITY_TYPE");
        addMetadata();
    }

    public QCapacityType(PathMetadata metadata) {
        super(QCapacityType.class, metadata, "PUBLIC", "CAPACITY_TYPE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

