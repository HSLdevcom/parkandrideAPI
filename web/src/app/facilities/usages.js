// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.usages', []);

    m.directive('usagesList', function (schema) {
        return {
            restrict: 'E',
            scope: {
                usages: '='
            },
            templateUrl: 'facilities/usagesList.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.usagesList = [];
                _.forEach(scope.usages, function(usage) {
                    scope.usagesList.push(schema.usages[usage]);
                });
            }
        };
    });
})();