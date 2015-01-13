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
                scope.capacityTypes = schema.capacityTypes;
                scope.hasCapacity = function(capacityType) {
                    return scope.capacities[capacityType];
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
                _.forEach(schema.capacityTypes, function(type) {
                    if (scope.capacities && scope.capacities[type]) {
                        scope.capacitiesList.push(_.extend({capacityType: type}, scope.capacities[type]));
                    }
                });
            }
        };
    });
})();