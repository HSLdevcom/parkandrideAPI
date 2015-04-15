// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.SchemaResource', []);

    m.factory('SchemaResource', function ($http, $q, Sequence) {
        var cached = {};

        var privateUrlPrefix = "internal/";
        var publicUrlPrefix = "api/v1/";

        function makeEnumLoader(type, urlPrefix) {
            return function() {
                if (cached[type]) {
                    var deferred = $q.defer();
                    deferred.resolve(cached[type]);
                    return deferred.promise;
                }

                return $http.get(urlPrefix + type).then(function(response) {
                    cached[type] = response.data;
                    return cached[type];
                });
            };
        }

        var api = {};

        api.getCapacityTypes = makeEnumLoader("capacity-types", publicUrlPrefix);

        api.getUsages = makeEnumLoader("usages", publicUrlPrefix);

        api.getDayTypes = makeEnumLoader("day-types", publicUrlPrefix);

        api.getServices = makeEnumLoader("services", publicUrlPrefix);

        api.getPaymentMethods = makeEnumLoader("payment-methods", publicUrlPrefix);

        api.getFacilityStatuses = makeEnumLoader("facility-statuses", publicUrlPrefix);

        api.getPricingMethods = makeEnumLoader("pricing-methods", publicUrlPrefix);

        api.getRoles = makeEnumLoader("roles", privateUrlPrefix);

        return api;
    });
})();
