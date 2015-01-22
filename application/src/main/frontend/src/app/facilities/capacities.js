(function() {
    var m = angular.module('parkandride.capacities', []);

    m.directive('capacitiesTable', function (schema) {
        return {
            restrict: 'E',
            scope: {
                capacities: '='
            },
            templateUrl: 'facilities/capacitiesTable.tpl.html',
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

    m.directive('capacitiesList', function (schema) {
        return {
            restrict: 'E',
            scope: {
                capacities: '='
            },
            templateUrl: 'facilities/capacitiesList.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.capacitiesList = [];
                _.forEach(schema.capacityTypes.values, function(type) {
                    if (scope.capacities && scope.capacities[type.id]) {
                        scope.capacitiesList.push(type);
                    }
                });
            }
        };
    });
})();