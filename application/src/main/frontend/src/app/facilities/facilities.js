(function() {
    var m = angular.module('ngBoilerplate.facilities', [
        'ui.router',
        'restangular',
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
    function FacilitiesController(Restangular) {
        var origThis = this;
        this.list = [];

        // TODO extract to service
        Restangular.one('facilities').get().then(function(data) {
            origThis.list = data.results;
        });
    }
    // TODO this should be done in directive (as it qualifies to output formatting)?
    FacilitiesController.prototype.capacityTypes = function(facility) {
        return Object.keys(facility.capacities);
    };
})();