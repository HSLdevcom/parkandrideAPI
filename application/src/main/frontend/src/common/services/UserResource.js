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

        api.resetToken = function(user) {
            return $http.put('internal/users/' + user.id + '/token').then(function(response){
                return response.data.value;
            });
        };

        api.updatePassword = function(user) {
            var config = {"skipDefaultViolationsHandling": true};
            var data = {value: user.password};
            return $http.put('internal/users/' + user.id + '/password', data, config);
        };

        return api;
    });
})();
