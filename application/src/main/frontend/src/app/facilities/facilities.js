(function() {
    var m = angular.module('ngBoilerplate.facilities', [
        'ui.router',
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

    m.controller('FacilitiesCtrl', ['$http', FacilitiesController]);
    function FacilitiesController($http) {
        var origThis = this;
        this.list = [];

        // TODO extract to service
        $http.get('/api/facilities').success(function (data) {
            origThis.list = data.results;
        });
    }
    FacilitiesController.prototype.capacityTypes = function(facility) {
        return Object.keys(facility.capacities);
    };
})();