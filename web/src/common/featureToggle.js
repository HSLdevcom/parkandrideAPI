// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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
        var api = {
            getFeatures: function() {
                return $http.get(FEATURES_URL).then(function (response) {
                    return response.data;
                });
            },
            isOn: function(name) {
                var defer = $q.defer();
                api.getFeatures().then(function(features) {
                    if (features[name] === true) {
                        defer.resolve();
                    } else {
                        defer.reject();
                    }
                });
               return defer.promise;
            }
        };
        return api;
    });
})();
