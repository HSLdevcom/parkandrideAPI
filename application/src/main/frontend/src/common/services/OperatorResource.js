// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.OperatorResource', []);

    m.factory('OperatorResource', function($http) {
        var api = {};

        api.getOperator = function(id) {
            return $http.get("api/v1/operators/" + id).then(function(response){
                return response.data;
            });
        };

        api.listOperators = function(search) {
            return $http.get("api/v1/operators", {
                params: search
            }).then(function(response) {
                return response.data;
            });
        };

        api.save = function(data) {
            var config = {"skipDefaultViolationsHandling": true};
            if (data.id) {
                return $http.put('api/v1/operators/' + data.id, data, config).then(function(response) {
                    return response.data;
                });
            } else {
                return $http.post('api/v1/operators', data, config).then(function(response) {
                    return response.data;
                });
            }
        };

        return api;
    });

})();