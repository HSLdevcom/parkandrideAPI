// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.util', []);

    m.directive('emptyToNull', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ctrl) {
                ctrl.$parsers.push(function(viewValue) {
                    return viewValue === '' ? null : viewValue;
                });
            }
        };
    });

    m.directive('focus', function () {
        return {
            link: function (scope, element) {
                // XXX: for some reason we can't focus the element synchronously
                setTimeout(function () {
                    element[0].focus();
                }, 100);
            }
        };
    });
})();
