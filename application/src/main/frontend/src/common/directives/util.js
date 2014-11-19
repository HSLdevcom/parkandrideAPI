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
})();
