package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QPaymentMethod is a Querydsl query type for QPaymentMethod
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPaymentMethod extends RelationalPathSpatial<QPaymentMethod> {

    private static final long serialVersionUID = 1642789532;

    public static final QPaymentMethod paymentMethod = new QPaymentMethod("PAYMENT_METHOD");

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QPaymentMethod> constraintD = createPrimaryKey(name);

    public final com.querydsl.sql.ForeignKey<QFacilityPaymentMethod> _facilityPaymentMethodPaymentMethodFk = createInvForeignKey(name, "PAYMENT_METHOD");

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

    public QPaymentMethod(PathMetadata metadata) {
        super(QPaymentMethod.class, metadata, "PUBLIC", "PAYMENT_METHOD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

