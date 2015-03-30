// Copyright © 2015 HSL

package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.PredictionRepository;
import fi.hsl.parkandride.core.domain.*;
import org.joda.time.DateTime;

import java.util.stream.Stream;

public class PredictionService {

    private final FacilityRepository facilityRepository;
    private final PredictionRepository predictionRepository;

    // TODO: inject predictors, lookup by id
    public Predictor predictor = new SameAsLatestPredictor();

    // TODO: save to database, lookup by id
    private PredictorState state = new PredictorState();

    public PredictionService(FacilityRepository facilityRepository, PredictionRepository predictionRepository) {
        this.facilityRepository = facilityRepository;
        this.predictionRepository = predictionRepository;
    }

    public void updatePredictions() {
        // TODO
        // - choose which sets of [predictor type, facility id, capacity type, usage] need to have their predictions updated
        //      - update interval of prediction type?
        //      - are there new utilization updates?
        // - block other servers from processing the same work set
        //      - use a message queue?
        //      - choose one at random and lock it?
        // - get predictor state
        // - run predictor
        // - save predictions
        //      - full log
        //      - lookup table
        // - save predictor state

        PredictionBatch batch = new PredictionBatch();
        batch.facilityId = 1L;
        batch.capacityType = CapacityType.CAR;
        batch.usage = Usage.PARK_AND_RIDE;

        UtilizationHistory history = new UtilizationHistory() {
            @Override
            public boolean hasUpdatesSince(DateTime startExclusive) {
                return getUpdatesSince(state.latestProcessed).findAny().isPresent();
            }

            @Override
            public Stream<Utilization> getUpdatesSince(DateTime startExclusive) {
                return facilityRepository.getStatuses(batch.facilityId).stream()
                        .filter(u -> u.timestamp.isAfter(startExclusive));
            }
        };

        if (history.hasUpdatesSince(state.latestProcessed)) {
            predictor.predict(state, batch, history);
            batch.sourceTimestamp = state.latestProcessed;
            predictionRepository.updatePredictions(batch);
        }
    }
}
