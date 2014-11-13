(function() {
    var m = angular.module('parkandride.multilingual', []);

    m.directive('multilingualEdit', function () {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                string: '=ngModel',
                mandatory: '@',
                name: '@'
            },
            templateUrl: 'directives/multilingualEdit.tpl.html',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                function toNullIfAllEmpty() {
                    if (scope.string && !scope.string.fi && !scope.string.sv && !scope.string.en) {
                        scope.string = null;
                    }
                }
                scope.$watch("string.fi", toNullIfAllEmpty);
                scope.$watch("string.sv", toNullIfAllEmpty);
                scope.$watch("string.en", toNullIfAllEmpty);
            }
        };
    });

    m.directive('multilingualView', function () {
        return {
            restrict: 'E',
            scope: {
                value: '=value'
            },
            templateUrl: 'directives/multilingualView.tpl.html',
            transclude: false
        };
    });
})();
