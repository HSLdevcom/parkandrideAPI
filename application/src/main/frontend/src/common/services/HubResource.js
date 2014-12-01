(function() {
    var m = angular.module('parkandride.HubResource', []);

    m.factory('HubResource', function($http) {
        var api = {};

        api.newHub = function() {
            return {
                facilityIds: []
            };
        };

        api.listHubs = function() {
            return $http.get('api/v1/hubs').then(function(response) {
                return response.data.results;
            });
        };

        api.getHub = function(id) {
            return $http.get('api/v1/hubs/' + id).then(function(response) {
                return response.data;
            });
        };

        api.save = function(data) {
            if (data.id) {
                return $http.put('api/v1/hubs/' + data.id, data).then(function(response) {
                    return response.data.id;
                });
            } else {
                return $http.post('api/v1/hubs', data).then(function(response) {
                    return response.data.id;
                });
            }
        };

        return api;
    });

})();