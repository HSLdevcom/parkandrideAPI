// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.SimpleExpression;
import fi.hsl.parkandride.back.sql.QPredictor;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.domain.PredictorState;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.service.TransactionalWrite;

public class PredictorDao implements PredictorRepository {

    private static final QPredictor qPredictor = QPredictor.predictor;

    public static final String PREDICTOR_ID_SEQ = "facility_id_seq";
    private static final SimpleExpression<Long> nextPredictorId = SQLExpressions.nextval(PREDICTOR_ID_SEQ);

    private final PostgresQueryFactory queryFactory;

    public PredictorDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public PredictorState enablePrediction(String predictorType, UtilizationKey utilizationKey) {
        PredictorState existing = queryFactory.from(qPredictor)
                .where(qPredictor.type.eq(predictorType),
                        qPredictor.facilityId.eq(utilizationKey.facilityId),
                        qPredictor.capacityType.eq(utilizationKey.capacityType),
                        qPredictor.usage.eq(utilizationKey.usage))
                .singleResult(new MappingProjection<PredictorState>(PredictorState.class, qPredictor.all()) {
                    @Override
                    protected PredictorState map(Tuple row) {
                        UtilizationKey utilizationKey = new UtilizationKey(row.get(qPredictor.facilityId), row.get(qPredictor.capacityType), row.get(qPredictor.usage));
                        PredictorState state = new PredictorState(row.get(qPredictor.type), utilizationKey);
                        state.predictorId = row.get(qPredictor.id);
                        // TODO: the rest of the fields
                        return state;
                    }
                });
        if (existing != null) {
            return existing;
        }
        queryFactory.insert(qPredictor)
                .set(qPredictor.id, queryFactory.query().singleResult(nextPredictorId))
                .set(qPredictor.type, predictorType)
                .set(qPredictor.facilityId, utilizationKey.facilityId)
                .set(qPredictor.capacityType, utilizationKey.capacityType)
                .set(qPredictor.usage, utilizationKey.usage)
                .execute();
        return enablePrediction(predictorType, utilizationKey);
    }
}
