(function() {
    var m = angular.module('parkandride.FacilityResource', []);

    function cleanupCapacities(capacities) {
        for (var capacityType in capacities) {
            if (!(capacities[capacityType] &&  capacities[capacityType].built && capacities[capacityType].built >= 1)) {
                delete capacities[capacityType];
            }
        }
    }

    m.factory('FacilityResource', function($http, $q) {
        var api = {};

        api.newFacility = function() {
            return {
                aliases: [],
                capacities: {}
            };
        };

        api.listFacilities = function(search) {
            return $http.get("/api/facilities", {
                params: search
            }).then(function(response) {
                return response.data.results;
            });
        };

        api.getFacility = function(id) {
            return $http.get("/api/facilities/" + id).then(function(response){
                return response.data;
            });
        };

        api.save = function(facility) {
            cleanupCapacities(facility.capacities);
            if (facility.id) {
                return $http.put("/api/facilities/" + facility.id, facility).then(function(response){
                    return response.data.id;
                });
            } else {
                return $http.post("/api/facilities", facility).then(function(response){
                    return response.data.id;
                });
            }
        };

        api.findFacilitiesAsFeatureCollection = function(search) {
            return $http.get("/api/facilities.geojson", {
                params: search
            }).then(function(response) {
                return response.data;
            });
        };

        api.summarizeFacilities = function(facilityIds) {
            if (_.isEmpty(facilityIds)){
                var deferred = $q.defer();
                deferred.resolve({ facilityCount: 0, capacities: {} });
                return deferred.promise;
            }

            return $http.get("/api/facilities", {
                params: { summary: true, ids: facilityIds }
            }).then(function(response) {
                return response.data;
            });
        };

        api.getCapacityTypes = function() {
            return $http.get("/api/capacity-types").then(function(response) {
                capacityTypesCached = response.data.results;
                return capacityTypesCached;
            });
        };

        return api;
    });

})();