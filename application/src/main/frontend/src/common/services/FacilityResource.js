(function() {
    var m = angular.module('parkandride.FacilityResource', [
        'restangular',
        'parkandride.resources.facilities'
    ]);

    m.factory('FacilityResource', function(Restangular, Facility) {
        var api = {};

        api.getFacilities = function() {
            return Restangular.one('facilities').get().then(function(data) {
                return data.results;
            });
        };

        api.getFacility = function(id) {
            return Restangular.one('facilities', id).get().then(function(data){
                return Facility.build(data);
            });
        };

        api.save = function(newFacility) {
            var data = Facility.toData(newFacility);
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

        api.getCapacityTypes = function() {
            return Restangular.one('capacity-types').get().then(function(data) {
                return data.results;
            });
        };

        return api;
    });
})();