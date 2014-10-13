(function() {
    var m = angular.module('parkandride.capacitiesTable', []);

    m.directive('capacitiesTable', function (MapService) {
        return {
            restrict: 'E',
            scope: {
                capacities: '='
            },
            templateUrl: 'facilities/capacitiesTable.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.thereAreCapacities = function() {
                    return !_.isEmpty(scope.capacities);
                };
            }
        };
    });
})();