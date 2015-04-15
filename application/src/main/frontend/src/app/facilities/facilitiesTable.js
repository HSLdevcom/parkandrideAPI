// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.facilitiesTable', [
        'parkandride.i18n',
        'parkandride.capacities',
        'parkandride.usages'
    ]);

    m.directive('facilitiesTable', function () {
        return {
            restrict: 'E',
            scope: {
                facilities: '='
            },
            templateUrl: 'facilities/facilitiesTable.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.thereAreFacilities = function() {
                    return !_.isEmpty(scope.facilities);
                };
            }
        };
    });
})();