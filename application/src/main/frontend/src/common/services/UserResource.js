(function() {
    var m = angular.module('parkandride.UserResource', []);

    m.factory('UserResource', function($http) {
        var api = {};

        api.listUsers = function() {
            return $http.get("internal/users").then(function(response) {
                return response.data;
            });
        };

        api.listRoles = function() {
            return $http.get("internal/roles").then(function(response) {
                return response.data.results;
            });
        };

        api.save = function(data) {
            var config = {"skipDefaultViolationsHandling": true};
            if (data.id) {
                return $http.put('internal/users/' + data.id, data, config).then(processResponse);
            }
            return $http.post('internal/users', data, config).then(processResponse);

            function processResponse(response) {
                return response.data;
            }
        };

        return api;
    });
})();
