// Copyright © 2016 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function () {
    var m = angular.module('parkandride.predictions', []);

    m.directive('predictionsTable', function (Ordering) {
        return {
            restrict: 'E',
            scope: {
                facility: '=',
                utilization: '=',
                predictions: '='
            },
            templateUrl: 'facilities/predictionsTable.tpl.html',
            transclude: false,
            link: function (scope) {

                scope.predictionTimes = _.chain(scope.predictions)
                    .flatten()
                    .map(function(row) {
                        return row.timestamp;
                    })
                    .sort()
                    .uniq(true)
                    .value();

                scope.rows = _.chain(scope.utilization)
                    .map(function (utilization) {
                        var capacityType = utilization.capacityType;
                        var usage = utilization.usage;
                        var predictions = _.chain(scope.predictions)
                            .flatten()
                            .filter(function (prediction) {
                                return prediction.capacityType === capacityType && prediction.usage === usage;
                            })
                            .reduce(function (memo, prediction) {
                                memo[prediction.timestamp] = prediction.spacesAvailable;
                                return memo;
                            }, {})
                            .value();
                        return {
                            capacityType: capacityType,
                            usage: usage,
                            capacity: scope.facility.builtCapacity[capacityType], // TODO: utilization specific capacity
                            utilization: {
                                spacesAvailable: utilization.spacesAvailable,
                                timestamp: utilization.timestamp
                            },
                            predictions: predictions
                        };
                    })
                    .sort(Ordering.byUsage(function (row) {
                        return row.usage;
                    }))
                    .sort(Ordering.byCapacityType(function (row) {
                        return row.capacityType;
                    }))
                    .value();
            }
        };
    });
})();