// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain.prediction;

import com.google.common.base.MoreObjects;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

public class HubPredictionResult extends BasePredictionResult {

    public long hubId;

    public static HubPredictionResult fromSingle(long hubId, PredictionResult prediction) {
        final HubPredictionResult hub = new HubPredictionResult();
        hub.hubId = hubId;
        hub.spacesAvailable = prediction.spacesAvailable;
        hub.timestamp = prediction.timestamp;
        hub.capacityType = prediction.capacityType;
        hub.usage = prediction.usage;
        return hub;
    }

    public static HubPredictionResult sumFrom(long hubId, PredictionResult... predictions) {
        return sumFrom(hubId, asList(predictions));
    }

    public static HubPredictionResult sumFrom(long hubId, Collection<PredictionResult> predictions) {
        // Check preconditions for summing
        expectUniqueResult(predictions.stream().map(pred -> pred.capacityType));
        expectUniqueResult(predictions.stream().map(pred -> pred.usage));
        expectUniqueResult(predictions.stream().map(pred -> pred.timestamp));

        return predictions.stream()
                .map(pred -> fromSingle(hubId, pred))
                .reduce((hub1, hub2) -> {
                    hub1.spacesAvailable = hub1.spacesAvailable + hub2.spacesAvailable;
                    return hub1;
                })
                .get();
    }

    private static <T> void expectUniqueResult(Stream<T> coll) {
        if (coll.collect(toSet()).size() != 1) {
            throw new IllegalArgumentException(String.format("Expected a collection with size of one, got <%s>", coll));
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hubId", hubId)
                .add("capacityType", capacityType)
                .add("usage", usage)
                .add("timestamp", timestamp)
                .add("spacesAvailable", spacesAvailable)
                .toString();
    }
}
