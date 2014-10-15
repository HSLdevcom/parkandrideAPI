(function() {
    var m = angular.module('featureToggle', []);

    function show(element) {
        element.removeClass('ng-hide');
    }

    function hide(element) {
        element.addClass('ng-hide');
    }

    m.directive('toggle', function(FeatureResource) {
        return {
            scope: {
                feature: '@'
            },
            restrict: 'A',
            link: function (scope, element, attrs) {
                hide(element);
                attrs.$observe('feature', function(value) {
                    FeatureResource.isOn(value).then(function() {
                        show(element);
                    }, function() {
                        hide(element);
                    });
                });
            }
        };
    });

    m.factory('FeatureResource', function($http, $q, FEATURES_URL) {
        return {
            isOn: function(name) {
                var defer = $q.defer();
                $http.get(FEATURES_URL).then(function(response) {
                    var features = response.data;
                    if (features[name] === true) {
                        defer.resolve();
                    } else {
                        defer.reject();
                    }
                });
               return defer.promise;
            }
        };
    });
})();
