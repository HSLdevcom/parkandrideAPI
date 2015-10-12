package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QFacilityPaymentMethod is a Querydsl query type for QFacilityPaymentMethod
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFacilityPaymentMethod extends RelationalPathSpatial<QFacilityPaymentMethod> {

    private static final long serialVersionUID = -130353223;

    public static final QFacilityPaymentMethod facilityPaymentMethod = new QFacilityPaymentMethod("FACILITY_PAYMENT_METHOD");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.PaymentMethod> paymentMethod = createEnum("paymentMethod", fi.hsl.parkandride.core.domain.PaymentMethod.class);

    public final com.querydsl.sql.PrimaryKey<QFacilityPaymentMethod> constraint31 = createPrimaryKey(facilityId, paymentMethod);

    public final com.querydsl.sql.ForeignKey<QPaymentMethod> facilityPaymentMethodPaymentMethodFk = createForeignKey(paymentMethod, "NAME");

    public final com.querydsl.sql.ForeignKey<QFacility> facilityPaymentMethodFacilityIdFk = createForeignKey(facilityId, "ID");

    public QFacilityPaymentMethod(String variable) {
        super(QFacilityPaymentMethod.class, forVariable(variable), "PUBLIC", "FACILITY_PAYMENT_METHOD");
        addMetadata();
    }

    public QFacilityPaymentMethod(String variable, String schema, String table) {
        super(QFacilityPaymentMethod.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityPaymentMethod(Path<? extends QFacilityPaymentMethod> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_PAYMENT_METHOD");
        addMetadata();
    }

    public QFacilityPaymentMethod(PathMetadata metadata) {
        super(QFacilityPaymentMethod.class, metadata, "PUBLIC", "FACILITY_PAYMENT_METHOD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(paymentMethod, ColumnMetadata.named("PAYMENT_METHOD").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

