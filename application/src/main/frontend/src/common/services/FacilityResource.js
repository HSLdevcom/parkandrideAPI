(function() {
    var m = angular.module('parkandride.FacilityResource', [
        'restangular'
    ]);

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

    m.factory('FacilityResource', function(Restangular) {
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
            return Restangular.one('facilities').get().then(function(data) {
                return data.results;
            });
        };

        api.getFacility = function(id) {
            return Restangular.one('facilities', id).get().then(function(data){
                return buildFacility(data);
            });
        };

        api.save = function(newFacility) {
            var data = facilityToData(newFacility);
            if (data.id) {
                return Restangular.one('facilities', data.id).customPUT(data).then(function(response){
                    return response.id;
                });
            } else {
                return Restangular.all('facilities').post(data).then(function(response){
                    return response.id;
                });
            }
        };

        /*
        api.findFacilitiesAsFeatures = function() {
            return api.listFacilities().then(function(results) {
                return _.map(results, function(facility) {
                    var clone = _.cloneDeep(facility);
                    var feature = {
                        type: "Feature",
                        geometry: clone.border,
                        properties: clone
                    };
                    delete clone.border;
                    return feature;
                });
            });
        };
        */

        api.getCapacityTypes = function() {
            return Restangular.one('capacity-types').get().then(function(data) {
                return data.results;
            });
        };

        return api;
    });

})();