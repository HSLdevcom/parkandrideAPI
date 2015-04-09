// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.SimpleExpression;
import fi.hsl.parkandride.back.sql.QPredictor;
import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.domain.PredictorState;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationService;

import java.util.List;
import java.util.Objects;

public class PredictorDao implements PredictorRepository {

    public static final String PREDICTOR_ID_SEQ = "facility_id_seq";
    private static final SimpleExpression<Long> nextPredictorId = SQLExpressions.nextval(PREDICTOR_ID_SEQ);

    private static final QPredictor qPredictor = QPredictor.predictor;

    private static final MappingProjection<PredictorState> predictorMapping = new MappingProjection<PredictorState>(PredictorState.class, qPredictor.all()) {
        @Override
        protected PredictorState map(Tuple row) {
            UtilizationKey utilizationKey = new UtilizationKey(row.get(qPredictor.facilityId), row.get(qPredictor.capacityType), row.get(qPredictor.usage));
            PredictorState state = new PredictorState(row.get(qPredictor.id), row.get(qPredictor.type), utilizationKey);
            state.latestUtilization = row.get(qPredictor.latestUtilization);
            state.moreUtilizations = row.get(qPredictor.moreUtilizations);
            state.internalState = row.get(qPredictor.internalState);
            return state;
        }
    };

    private static Predicate[] utilizationKeyEquals(UtilizationKey utilizationKey) {
        return new Predicate[]{
                qPredictor.facilityId.eq(utilizationKey.facilityId),
                qPredictor.capacityType.eq(utilizationKey.capacityType),
                qPredictor.usage.eq(utilizationKey.usage)
        };
    }


    private final PostgresQueryFactory queryFactory;
    private final ValidationService validationService;

    public PredictorDao(PostgresQueryFactory queryFactory, ValidationService validationService) {
        this.queryFactory = queryFactory;
        this.validationService = validationService;
    }

    @TransactionalWrite
    @Override
    public PredictorState enablePrediction(String predictorType, UtilizationKey utilizationKey) {
        validationService.validate(utilizationKey);
        PredictorState existing = queryFactory.from(qPredictor)
                .where(qPredictor.type.eq(predictorType))
                .where(utilizationKeyEquals(utilizationKey))
                .singleResult(predictorMapping);
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

    @TransactionalWrite
    @Override
    public void save(PredictorState state) {
        validationService.validate(state);
        queryFactory.update(qPredictor)
                .set(qPredictor.latestUtilization, state.latestUtilization)
                .set(qPredictor.moreUtilizations, state.moreUtilizations)
                .set(qPredictor.internalState, state.internalState)
                .where(qPredictor.id.eq(state.predictorId))
                .execute();
    }

    @TransactionalRead
    @Override
    public PredictorState getById(Long predictorId) {
        return Objects.requireNonNull(
                queryFactory.from(qPredictor)
                        .where(qPredictor.id.eq(predictorId))
                        .singleResult(predictorMapping),
                "No predictors with id " + predictorId);
    }

    @TransactionalRead
    @Override
    public List<PredictorState> findPredictorsNeedingUpdate() {
        return queryFactory.from(qPredictor)
                .where(qPredictor.moreUtilizations.eq(true))
                .list(predictorMapping);
    }

    @TransactionalWrite
    @Override
    public void markPredictorsNeedAnUpdate(UtilizationKey utilizationKey) {
        validationService.validate(utilizationKey);
        queryFactory.update(qPredictor)
                .set(qPredictor.moreUtilizations, true)
                .where(utilizationKeyEquals(utilizationKey))
                .execute();
    }
}
