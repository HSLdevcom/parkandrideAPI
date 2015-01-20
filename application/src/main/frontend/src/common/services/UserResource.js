(function() {
    var m = angular.module('parkandride.UserResource', []);

    m.factory('UserResource', function($http) {
        var api = {};

        api.listUsers = function() {
            return $http.get("internal/users").then(function(response) {
                return response.data;
            });
        };

        return api;
    });
})();
