// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.predictions', []);

    m.directive('predictionsTable', function (schema) {
        return {
            restrict: 'E',
            scope: {
                predictions: '='
            },
            templateUrl: 'facilities/predictionsTable.tpl.html',
            transclude: false,
            link: function(scope) {
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