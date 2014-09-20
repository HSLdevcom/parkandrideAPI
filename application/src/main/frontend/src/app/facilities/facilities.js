angular.module('ngBoilerplate.facilities', [
    'ui.router',
    'facilities.create',
    'facilities.view'
])

    .config(function config($stateProvider) {
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
    })

    .controller('FacilitiesCtrl', [ '$scope', '$http', function FacilitiesController($scope, $http) {
        var facilities = this;
        facilities.list = [];

        facilities.capacityTypes = function(facility) {
            return Object.keys(facility.capacities);
        };

        $http.get('/api/facilities').success(function(data){
            facilities.list = data.results;
        });
    } ])
;

