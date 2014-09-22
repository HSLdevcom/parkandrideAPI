(function(){
    var m = angular.module('ngBoilerplate.services.facilities', [
        'restangular',
        'ngBoilerplate.resources.facilities'
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
            Restangular.all('facilities').post(data);
        };

        return api;
    });
})();