(function() {
    var m = angular.module('parkandride.OperatorResource', []);

    m.factory('OperatorResource', function($http) {
        var api = {};

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