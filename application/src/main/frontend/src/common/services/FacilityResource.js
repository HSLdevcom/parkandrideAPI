(function() {
    var m = angular.module('parkandride.FacilityResource', []);

    function buildCapacities(data) {
        return _.reduce(
            data,
            function(target, capacity, key) {
                var copy = _.clone(capacity);
                copy.capacityType = key;
                target.push(copy);
                return target;
            },
            []);
    }

    function capacitiesToData(capacities) {
        return _.reduce(
            capacities,
            function(target, capacity) {
                if (capacity.built > 0) {
                    var copy = _.clone(capacity);
                    delete copy.capacityType;
                    target[capacity.capacityType] = copy;
                }
                return target;
            },
            {});
    }

    function buildFacility(data) {
        var copy = _.clone(data);
        copy.capacities = buildCapacities(data.capacities);
        return copy;
    }

    function facilityToData(facility) {
        var copy = _.clone(facility);
        copy.capacities = capacitiesToData(facility.capacities);
        return copy;
    }

    m.factory('FacilityResource', function($http) {
        var api = {};

        api.newFacility = function() {
            return {
                aliases: [],
                capacities: {}
            };
        };

        api.getOrCreateCapacity = function(facility, capacityType) {
            return _.find(facility.capacities, function(c) { return c.capacityType == capacityType; }) || { capacityType: capacityType };
        };

        api.listFacilities = function() {
            return $http.get("/api/facilities").then(function(response) {
                return response.data.results;
            });
        };

        api.getFacility = function(id) {
            return $http.get("/api/facilities/" + id).then(function(response){
                return buildFacility(response.data);
            });
        };

        api.save = function(newFacility) {
            var data = facilityToData(newFacility);
            if (data.id) {
                return $http.put("/api/facilities/" + data.id, data).then(function(response){
                    return response.data.id;
                });
            } else {
                return $http.post("/api/facilities", data).then(function(response){
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

        api.summarizeFacilities = function(search) {
            var params = _.cloneDeep(search);
            params.summary = true;
            return $http.get("/api/facilities", {
                params: params
            }).then(function(response) {
                var clone = _.cloneDeep(response.data);
                clone.capacities = buildCapacities(clone.capacities);
                return clone;
            });
        };

        api.getCapacityTypes = function() {
            return $http.get("/api/capacity-types").then(function(response) {
                return response.data.results;
            });
        };

        return api;
    });

})();