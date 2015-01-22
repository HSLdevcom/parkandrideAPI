(function() {
    var m = angular.module('parkandride.SchemaResource', []);

    m.factory('SchemaResource', function ($http, $q, Sequence) {
        var cached = {};

        function makeEnumLoader(type) {
            return function() {
                if (cached[type]) {
                    var deferred = $q.defer();
                    deferred.resolve(cached[type]);
                    return deferred.promise;
                }
                return $http.get("api/v1/" + type).then(function(response) {
                    cached[type] = response.data;
                    return cached[type];
                });
            };
        }

        var api = {};
        api.getCapacityTypes = makeEnumLoader("capacity-types");

        api.getUsages = makeEnumLoader("usages");

        api.getDayTypes = makeEnumLoader("day-types");

        api.getServices = makeEnumLoader("services");

        api.getPaymentMethods = makeEnumLoader("payment-methods");

        return api;
    });
})();