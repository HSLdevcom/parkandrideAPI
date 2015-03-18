// Copyright © 2015 HSL

(function() {
    var m = angular.module('showErrors', []);

    m.directive('showErrors', function($interpolate, EVENTS) {
        var errorClass = "validation-error";

        function linkFn(scope, el, attrs, formCtrl) {
            var blurred = false;
            var options = scope.$eval(attrs.showErrors) || {};

            function toggleClasses(invalid) {
                el.toggleClass(errorClass, invalid);
            }

            var elName = $interpolate(el.attr('name'))(scope);
            if (!elName) {
                throw "show-errors element has no 'name' attribute";
            }

            el.bind('blur', function() {
                blurred = true;
                return toggleClasses(formCtrl[elName].$invalid);
            });

            scope.$watch(function() {
                return formCtrl[elName] && formCtrl[elName].$invalid;
            }, function(invalid) {
                if (options.instant) {
                    if (!formCtrl[elName].$touched) {
                        return;
                    }
                    return toggleClasses(invalid);
                }

                if (!blurred) {
                    return;
                }
                return toggleClasses(invalid);
            });

            scope.$on(EVENTS.showErrorsCheckValidity, function() {
                return toggleClasses(formCtrl[elName].$invalid);
            });

            return toggleClasses;
        }

        return {
            restrict: 'A',
            require: '^form',
            link: linkFn
        };
    });
})();