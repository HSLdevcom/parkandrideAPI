(function() {
    var m = angular.module('ngBoilerplate.facilities', [
        'ui.router',
        'ngBoilerplate.services.facilities',
        'facilities.create',
        'facilities.view'
    ]);

    m.config(function config($stateProvider) {
            $stateProvider.state('facilities', {
                url: '/facilities',
                views: {
                    "main": {
                        controller: 'FacilitiesCtrl',
                        templateUrl: 'facilities/facilities.tpl.html'
                    }
                },
                data: { pageTitle: 'Facilities' }
            });
        });

    m.controller('FacilitiesCtrl', FacilitiesController);
    function FacilitiesController(FacilityService) {
        var origThis = this;
        this.list = [];

        FacilityService.getFacilities().then(function(data){
            origThis.list = data;
        });
    }
    // TODO this should be done in directive (as it qualifies to output formatting)?
    FacilitiesController.prototype.capacityTypes = function(facility) {
        return Object.keys(facility.capacities);
    };
})();