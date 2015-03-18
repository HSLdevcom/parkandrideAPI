// Copyright © 2015 HSL

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
