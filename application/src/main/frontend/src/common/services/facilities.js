(function() {
    var m = angular.module('parkandride.services.facilities', [
        'restangular',
        'parkandride.resources.facilities'
    ]);

    m.factory('FacilityService', function(Restangular, Facility) {
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
            return Restangular.all('facilities').post(data).then(function(response){
                return response.id;
            });
        };

        return api;
    });
})();