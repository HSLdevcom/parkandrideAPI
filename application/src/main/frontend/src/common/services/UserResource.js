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

        return api;
    });
})();
