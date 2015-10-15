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
 * QPricingMethod is a Querydsl query type for QPricingMethod
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPricingMethod extends RelationalPathSpatial<QPricingMethod> {

    private static final long serialVersionUID = -1393682340;

    public static final QPricingMethod pricingMethod = new QPricingMethod("PRICING_METHOD");

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QPricingMethod> constraintF = createPrimaryKey(name);

    public final com.querydsl.sql.ForeignKey<QFacility> _facilityPricingMethodFk = createInvForeignKey(name, "PRICING_METHOD");

    public QPricingMethod(String variable) {
        super(QPricingMethod.class, forVariable(variable), "PUBLIC", "PRICING_METHOD");
        addMetadata();
    }

    public QPricingMethod(String variable, String schema, String table) {
        super(QPricingMethod.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPricingMethod(Path<? extends QPricingMethod> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PRICING_METHOD");
        addMetadata();
    }

    public QPricingMethod(PathMetadata metadata) {
        super(QPricingMethod.class, metadata, "PUBLIC", "PRICING_METHOD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

