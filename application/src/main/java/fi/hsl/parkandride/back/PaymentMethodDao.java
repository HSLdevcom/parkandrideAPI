package fi.hsl.parkandride.back;

import java.util.List;

import com.mysema.query.Tuple;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;

import fi.hsl.parkandride.back.sql.QPaymentMethod;
import fi.hsl.parkandride.core.back.PaymentMethodRepository;
import fi.hsl.parkandride.core.domain.PaymentMethod;
import fi.hsl.parkandride.core.service.TransactionalRead;

public class PaymentMethodDao implements PaymentMethodRepository {
    private static QPaymentMethod qPaymentMethod = QPaymentMethod.paymentMethod;
    private static MultilingualStringMapping nameMapping = new MultilingualStringMapping(qPaymentMethod.nameFi, qPaymentMethod.nameSv, qPaymentMethod.nameEn);

    private static MappingProjection<PaymentMethod> paymentMethodMapping = new MappingProjection<PaymentMethod>(PaymentMethod.class, qPaymentMethod.all()) {
        @Override
        protected PaymentMethod map(Tuple row) {
            Long id = row.get(qPaymentMethod.id);
            if (id == null) {
                return null;
            }
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.id = id;
            paymentMethod.name = nameMapping.map(row);
            return paymentMethod;
        }
    };

    private final PostgresQueryFactory queryFactory;

    public PaymentMethodDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalRead
    public List<PaymentMethod> findAll() {
        PostgresQuery qry = queryFactory.from(qPaymentMethod);
        qry.orderBy(qPaymentMethod.id.asc());
        return qry.list(paymentMethodMapping);
    }
}
