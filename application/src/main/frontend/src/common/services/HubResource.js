(function() {
    var m = angular.module('parkandride.HubResource', [
        'restangular'
    ]);

    m.factory('HubResource', function(Restangular) {
        var api = {};

        api.newHub = function() {
            return {};
        };

        api.listHubs = function() {
            return Restangular.one('hubs').get().then(function(data) {
                return data.results;
            });
        };

        api.getHub = function(id) {
            return Restangular.one('hubs', id).get().then(function(data) {
                return data;
            });
        };

        api.save = function(data) {
            if (data.id) {
                return Restangular.one('hubs', data.id).customPUT(data).then(function(response) {
                    return response.id;
                });
            } else {
                return Restangular.all('hubs').post(data).then(function(response) {
                    return response.id;
                });
            }
        };

        return api;
    });

})();