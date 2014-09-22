(function() {
    var m = angular.module('facilities.view', [
        'ui.router',
        'restangular'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facilities-view', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/view/:id', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'ViewCtrl',
                    templateUrl: 'facilities/view.tpl.html',
                    resolve: {
                        facility: function($stateParams, Restangular) {
                            // TODO put to service
                            return Restangular.one('facilities', $stateParams.id).get();
                        }
                    }
                }
            },
            data: { pageTitle: 'View Facility' }
        });
    });

    m.controller('ViewCtrl', ViewController);
    function ViewController(facility, $log) {
        this.facility = facility;
        $log.info(facility);
    }
})();