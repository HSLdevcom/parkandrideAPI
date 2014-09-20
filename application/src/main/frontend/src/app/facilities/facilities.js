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

    .controller('FacilitiesCtrl', function FacilitiesController($scope) {
    })
;

