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
 * QPaymentMethod is a Querydsl query type for QPaymentMethod
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPaymentMethod extends RelationalPathSpatial<QPaymentMethod> {

    private static final long serialVersionUID = 1642789532;

    public static final QPaymentMethod paymentMethod = new QPaymentMethod("PAYMENT_METHOD");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QPaymentMethod> constraintD = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QFacilityPaymentMethod> _facilityPaymentMethodPaymentMethodFk = createInvForeignKey(name, "PAYMENT_METHOD");

    public QPaymentMethod(String variable) {
        super(QPaymentMethod.class, forVariable(variable), "PUBLIC", "PAYMENT_METHOD");
        addMetadata();
    }

    public QPaymentMethod(String variable, String schema, String table) {
        super(QPaymentMethod.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPaymentMethod(Path<? extends QPaymentMethod> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PAYMENT_METHOD");
        addMetadata();
    }

    public QPaymentMethod(PathMetadata<?> metadata) {
        super(QPaymentMethod.class, metadata, "PUBLIC", "PAYMENT_METHOD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

