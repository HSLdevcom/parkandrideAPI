(function() {
    var m = angular.module('parkandride', [
        'templates-app',
        'templates-common',

        'ui.router',
        'restangular',

        'filters',

        'parkandride.facilityList'
    ]);

    m.config(function myAppConfig($stateProvider, $urlRouterProvider, RestangularProvider) {
        $urlRouterProvider.otherwise('/facilities');
        RestangularProvider.setBaseUrl('/api');
    });

    m.run(function run() {
        // Use the main applications run method to execute any code after services have been instantiated
    });

    m.controller('AppCtrl', function AppCtrl($scope, $location) {
        // This is a good place for logic not specific to the template or route, such as menu logic or page title wiring
        $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
            if (angular.isDefined(toState.data.pageTitle)) {
                $scope.pageTitle = toState.data.pageTitle + ' | parkandride';
            }
        });
    });
})();
