(function() {
    var m = angular.module('parkandride.ContactResource', []);

    m.factory('ContactResource', function($http) {
        var api = {};

        api.listContacts = function(search) {
            return $http.get("/api/contacts", {
                params: search
            }).then(function(response) {
                return response.data;
            });
        };

        api.save = function(data) {
            var config = {"skipDefaultViolationsHandling": true};
            if (data.id) {
                return $http.put('/api/contacts/' + data.id, data, config).then(function(response) {
                    return response.data.id;
                });
            } else {
                return $http.post('/api/contacts', data, config).then(function(response) {
                    return response.data.id;
                });
            }
        };

        return api;
    });

})();