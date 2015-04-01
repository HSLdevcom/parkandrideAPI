// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.back.PredictorRepository;
import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.PredictorState;
import fi.hsl.parkandride.core.domain.Usage;
import fi.hsl.parkandride.core.domain.UtilizationKey;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class PredictorDaoTest extends AbstractDaoTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Inject Dummies dummies;
    @Inject PredictorRepository predictorRepository;

    private long facilityId;
    private UtilizationKey utilizationKey;

    @Before
    public void initTestData() {
        facilityId = dummies.createFacility();
        utilizationKey = new UtilizationKey(facilityId, CapacityType.CAR, Usage.PARK_AND_RIDE);
    }


    // enabling predictors

    @Test
    public void creates_predictor_when_enabled_for_the_first_time() {
        PredictorState state = predictorRepository.enablePrediction("the-type", utilizationKey);

        // identifying fields:
        assertThat(state.predictorId).as("predictorId").isNotNull();
        assertThat(state.predictorType).as("predictorType").isEqualTo("the-type");
        assertThat(state.utilizationKey.facilityId).as("facilityId").isEqualTo(facilityId);
        assertThat(state.utilizationKey.capacityType).as("capacityType").isEqualTo(CapacityType.CAR);
        assertThat(state.utilizationKey.usage).as("usage").isEqualTo(Usage.PARK_AND_RIDE);
        // default values:
        assertThat(state.latestUtilization).as("latestUtilization").isEqualTo(new DateTime(0)); // safe default: before all possible utilizations
        assertThat(state.moreUtilizations).as("moreUtilizations").isTrue(); // safe default: assume there are some utilizations to process
        assertThat(state.internalState).as("internalState").isEqualTo("");
        // keep defaults in sync with Java code:
        PredictorState javaDefaults = new PredictorState(null, null);
        assertThat(javaDefaults.latestUtilization).as("javaDefaults.latestUtilization").isEqualTo(state.latestUtilization);
        assertThat(javaDefaults.moreUtilizations).as("javaDefaults.moreUtilizations").isEqualTo(state.moreUtilizations);
        assertThat(javaDefaults.internalState).as("javaDefaults.internalState").isEqualTo(state.internalState);
    }

    @Test
    public void returns_existing_predictor_when_enabled_for_the_second_time() {
        PredictorState state1 = predictorRepository.enablePrediction("predictor-type", utilizationKey);
        PredictorState state2 = predictorRepository.enablePrediction("predictor-type", utilizationKey);

        assertThat(state2.predictorId).as("state2.predictorId").isEqualTo(state1.predictorId);
    }

    // TODO: find predictions with new utilizations
    // TODO: mark prediction as having new utilizations
    // TODO: save predictor state
    // TODO: validate predictor state before saving


    // uniqueness

    @Test
    public void predictors_are_predictor_type_specific() {
        PredictorState state1 = predictorRepository.enablePrediction("type1", utilizationKey);
        PredictorState state2 = predictorRepository.enablePrediction("type2", utilizationKey);

        assertThat(state2.predictorId).as("state2.predictorId").isNotEqualTo(state1.predictorId);
    }

    @Test
    public void predictors_are_facility_specific() {
        long facilityId2 = dummies.createFacility();
        PredictorState state1 = predictorRepository.enablePrediction("predictor-type", new UtilizationKey(facilityId, CapacityType.CAR, Usage.PARK_AND_RIDE));
        PredictorState state2 = predictorRepository.enablePrediction("predictor-type", new UtilizationKey(facilityId2, CapacityType.CAR, Usage.PARK_AND_RIDE));

        assertThat(state2.predictorId).as("state2.predictorId").isNotEqualTo(state1.predictorId);
    }

    @Test
    public void predictors_are_capacity_type_specific() {
        PredictorState state1 = predictorRepository.enablePrediction("predictor-type", new UtilizationKey(facilityId, CapacityType.CAR, Usage.PARK_AND_RIDE));
        PredictorState state2 = predictorRepository.enablePrediction("predictor-type", new UtilizationKey(facilityId, CapacityType.ELECTRIC_CAR, Usage.PARK_AND_RIDE));

        assertThat(state2.predictorId).as("state2.predictorId").isNotEqualTo(state1.predictorId);
    }

    @Test
    public void predictors_are_usage_specific() {
        PredictorState state1 = predictorRepository.enablePrediction("predictor-type", new UtilizationKey(facilityId, CapacityType.CAR, Usage.PARK_AND_RIDE));
        PredictorState state2 = predictorRepository.enablePrediction("predictor-type", new UtilizationKey(facilityId, CapacityType.CAR, Usage.COMMERCIAL));

        assertThat(state2.predictorId).as("state2.predictorId").isNotEqualTo(state1.predictorId);
    }
}
