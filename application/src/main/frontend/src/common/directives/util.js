// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.util', []);

    m.service('Ordering', function(schema) {
        var self = this;

        function getId(v) { return v.id; }
        function byIndexOf(array) {
            function returnArg1(arg1) { return arg1; }
            return function(getter) {
                var _getter = getter || returnArg1;
                return function(a,b) {
                    return array.indexOf(_getter(a)) - array.indexOf(_getter(b));
                };
            };
        }

        var capacityOrder = schema.capacityTypes.values.map(getId);
        var usageOrder = schema.usages.values.map(getId);

        self.byCapacityType = byIndexOf(capacityOrder);
        self.byUsage = byIndexOf(usageOrder);
    });

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
