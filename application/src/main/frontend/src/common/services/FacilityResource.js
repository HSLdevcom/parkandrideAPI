(function() {
    var m = angular.module('parkandride.FacilityResource', [
        'parkandride.Sequence'
    ]);

    m.factory('FacilityResource', function($http, $q, Sequence) {

        function cleanupCapacities(capacities) {
            for (var capacityType in capacities) {
                if (!capacities[capacityType]) {
                    delete capacities[capacityType];
                }
            }
        }

        function cleanupPorts(ports) {
            for (var i=0; i < ports.length; i++) {
                delete ports[i]._id;
            }
        }

        function typeUsageKey(capacityType, usage) {
            return capacityType + "/" + usage;
        }

        function cleanupUnavailableCapacities(unavailableCapacities, pricing) {
            if (unavailableCapacities && unavailableCapacities.length > 0) {
                var typeUsages = {};
                _.forEach(pricing, function(p) {
                    typeUsages[typeUsageKey(p.capacityType, p.usage)] = true;
                });
                _.remove(unavailableCapacities, function(uc) {
                    return !typeUsages[typeUsageKey(uc.capacityType, uc.usage)];
                });
            }
        }

        function assignPortIds(facility) {
            for (var i = 0; i < facility.ports.length; i++) {
                facility.ports[i]._id = Sequence.nextval();
            }
            return facility;
        }
        function addFacilityIndexes(facilities) {
            _.forEach(facilities, function(f, index) {
                f._index = index;
            });
            return facilities;
        }

        function addFacilityIndexesToFeatures(featureCollection) {
            _.forEach(featureCollection.features, function(f, index) {
                f.properties._index = index;
            });
            return featureCollection;
        }

        var api = {};

        api.newFacility = function() {
            return {
                aliases: [],
                builtCapacity: {},
                pricing: [
                    { dayType: 'BUSINESS_DAY' },
                    { dayType: 'SATURDAY' },
                    { dayType: 'SUNDAY' },
                    { dayType: 'HOLIDAY' },
                    { dayType: 'EVE' }
                ],
                unavailableCapacities: [],
                ports: [],
                contacts: {}
            };
        };

        api.newPort = function(location) {
            return {
                location: location,
                entry: true,
                exit: true,
                pedestrian: false,
                bicycle: false
            };
        };

        api.listFacilities = function(search) {
            return $http.get("api/v1/facilities", {
                params: search
            }).then(function(response) {
                return addFacilityIndexes(response.data.results);
            });
        };

        api.getFacility = function(id) {
            return $http.get("api/v1/facilities/" + id).then(function(response){
                return assignPortIds(response.data);
            });
        };

        api.save = function(facility) {
            cleanupCapacities(facility.builtCapacity);
            cleanupPorts(facility.ports);
            cleanupUnavailableCapacities(facility.unavailableCapacities, facility.pricing);
            if (facility.id) {
                return $http.put("api/v1/facilities/" + facility.id, facility).then(function(response){
                    return response.data.id;
                });
            } else {
                return $http.post("api/v1/facilities", facility).then(function(response){
                    return response.data.id;
                });
            }
        };

        api.findFacilitiesAsFeatureCollection = function(search) {
            return $http.get("api/v1/facilities.geojson", {
                params: search
            }).then(function(response) {
                return addFacilityIndexesToFeatures(response.data);
            });
        };

        api.loadFacilities = function(facilityIds) {
            if (_.isEmpty(facilityIds)){
                var deferred = $q.defer();
                deferred.resolve([]);
                return deferred.promise;
            }
            return api.listFacilities({ ids: facilityIds });
        };

        api.summarizeFacilities = function(facilityIds) {
            if (_.isEmpty(facilityIds)){
                var deferred = $q.defer();
                deferred.resolve({ facilityCount: 0, capacities: {} });
                return deferred.promise;
            }

            return $http.get("api/v1/facilities", {
                params: { summary: true, ids: facilityIds }
            }).then(function(response) {
                return response.data;
            });
        };

        return api;
    });

})();