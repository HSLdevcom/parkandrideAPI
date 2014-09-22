(function(){
    var m = angular.module('ngBoilerplate.services.facilities', ['restangular']);

    m.factory('FacilityService', function(Restangular) {
        var api = {};

        api.getFacilities = function() {
            return Restangular.one('facilities').get().then(function(data) {
                return data.results;
            });
        };

        api.getFacility = function(id) {
            return Restangular.one('facilities', id).get();
        };

        api.save = function(newFacility) {
            Restangular.all('facilities').post(newFacility);
        };

        return api;
    });
})();