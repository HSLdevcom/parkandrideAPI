// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function () {
    var m = angular.module('parkandride.predictions', []);

    m.directive('predictionsTable', function (Ordering) {
        return {
            restrict: 'E',
            scope: {
                facility: '=',
                utilization: '=',
                predictionsData: '=predictions'
            },
            templateUrl: 'facilities/predictionsTable.tpl.html',
            transclude: false,
            link: function (scope) {

                function capacityTypeAndUsage(pred) {
                    return pred.capacityType + pred.usage;
                }

                function getUtilization(capacityType, usage) {
                    return _.find(scope.utilization, { capacityType: capacityType, usage: usage });
                }

                scope.predictionTimes = _.chain(scope.predictionsData)
                    .flatten()
                    .map(function(row) {
                        return row.timestamp;
                    })
                    .sort()
                    .uniq(true)
                    .value();

                scope.predictions = _.chain(scope.predictionsData)
                    .flatten()
                    .groupBy(capacityTypeAndUsage)
                    .map(function (groupedPredictions) {
                        var singleItem = groupedPredictions.reduce(function (row, newPrediction) {
                            row.capacityType = newPrediction.capacityType;
                            row.usage = newPrediction.usage;
                            row.capacity = scope.facility.builtCapacity[row.capacityType];
                            row.predictions[newPrediction.forecastDistanceInMinutes] = newPrediction.spacesAvailable;
                            row.predictions[newPrediction.timestamp] = newPrediction.spacesAvailable;
                            return row;
                        }, {predictions: {}});
                        singleItem.utilization = getUtilization(singleItem.capacityType, singleItem.usage);
                        return singleItem;
                    })
                    .sort(Ordering.byUsage(function(val) { return val.usage; }))
                    .sort(Ordering.byCapacityType(function(val) { return val.capacityType; }))
                    .value();
            }
        };
    });
})();