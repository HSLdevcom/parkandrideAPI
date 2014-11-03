(function() {
    var m = angular.module('showErrors', []);

    m.directive('showErrors', function($interpolate) {
        var errorClass = "validation-error";

        function linkFn(scope, el, attrs, formCtrl) {
            var blurred = false;

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
                if (!blurred) {
                    return;
                }
                return toggleClasses(invalid);
            });

            scope.$on('show-errors-check-validity', function() {
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