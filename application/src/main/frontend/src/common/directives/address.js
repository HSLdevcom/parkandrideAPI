// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.address', []);

    m.directive('addressEdit', function () {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                address: '=ngModel',
                path: '@'
            },
            templateUrl: 'directives/addressEdit.tpl.html',
            transclude: false
        };
    });

    m.directive('addressView', function () {
        return {
            restrict: 'E',
            scope: {
                address: '='
            },
            templateUrl: 'directives/addressView.tpl.html',
            transclude: false
        };
    });
})();
