(function() {
    var m = angular.module('facilities.create', [
        'ui.router'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facilities-create', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/create', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'CreateCtrl',
                    templateUrl: 'facilities/create.tpl.html'
                }
            },
            data: { pageTitle: 'Create Facility' }
        });
    });

    m.controller('CreateCtrl', CreateController);
    function CreateController() {}
})();

