angular.module('ngBoilerplate.facilities.create', [
    'ui.router'
])

    .config(function config($stateProvider) {
        $stateProvider.state('facilities.create', {
            url: '/facilities/create',
            views: {
                "main": {
                    controller: 'CreateCtrl',
                    templateUrl: 'facilities/create/create.tpl.html'
                }
            },
            data: { pageTitle: 'Create Facility' }
        });
    })

    .controller('CreateCtrl', function CreateController($scope) {
    })
;

