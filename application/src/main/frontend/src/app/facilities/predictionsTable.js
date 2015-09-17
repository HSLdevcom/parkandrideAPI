// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.predictions', []);

    m.directive('predictionsTable', function (schema) {
        return {
            restrict: 'E',
            scope: {
                facility: '=',
                predictionsData: '=predictions'
            },
            templateUrl: 'facilities/predictionsTable.tpl.html',
            transclude: false,
            link: function(scope) {

                function capacityTypeAndUsage(pred) {
                    return pred.capacityType + pred.usage;
                }

                scope.predictions = _.chain(scope.predictionsData)
                        .flatten()
                        .groupBy(capacityTypeAndUsage)
                        .map(function(row) {
                            return row.reduce(function (row, newPrediction) {
                                row.capacityType = newPrediction.capacityType;
                                row.usage = newPrediction.usage;
                                row.capacity = scope.facility.builtCapacity[row.capacityType];
                                row.predictions[newPrediction.forecastDistanceInMinutes] = newPrediction.spacesAvailable;
                                return row;
                            }, { predictions: {} });
                        })
                        .value();

                scope.capacityTypes = schema.capacityTypes.values;
                scope.hasCapacity = function(capacityType) {
                    return scope.capacities[capacityType.id];
                };
                scope.thereAreCapacities = function() {
                    return !_.isEmpty(scope.capacities);
                };
            }
        };
    });
})();