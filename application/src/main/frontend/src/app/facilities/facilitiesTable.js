(function() {
    var m = angular.module('parkandride.facilitiesTable', [
        'parkandride.i18n'
    ]);

    m.directive('facilitiesTable', function ($translate) {
        return {
            restrict: 'E',
            scope: {
                facilities: '='
            },
            templateUrl: 'facilities/facilitiesTable.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.translatedCapacities = function(facility) {
                    // FIXME: Refactor translate into i18n module!
                    return _.values($translate.instant(_.map(Object.keys(facility.capacities), function (capacityType) {
                        return "facilities.capacityType." + capacityType;
                    })));
                };
                scope.thereAreFacilities = function() {
                    return !_.isEmpty(scope.facilities);
                };
            }
        };
    });
})();