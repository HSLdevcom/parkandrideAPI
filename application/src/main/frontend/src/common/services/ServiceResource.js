(function() {
    var m = angular.module('parkandride.ServiceResource', []);

    m.factory('ServiceResource', function($http) {
        var api = {};

        api.listServices = function(search) {
            return $http.get("api/v1/services", {
                params: search
            }).then(function(response) {
                return response.data;
            });
        };

        return api;
    });

})();