// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.RegionResource', []);

    m.factory('RegionResource', function($http) {
        var api = {};

        api.listRegions = function() {
            return $http.get('api/v1/regions').then(function(response) {
                return response.data;
            });
        };

        return api;
    });

})();