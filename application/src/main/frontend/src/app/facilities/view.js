(function() {
    var m = angular.module('facilities.view', [
        'ui.router'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facilities-view', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/view', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'ViewCtrl',
                    templateUrl: 'facilities/view.tpl.html'
                }
            },
            data: { pageTitle: 'View Facility' }
        });
    });

    m.controller('ViewCtrl', ViewController);
    function ViewController() {}
})();