(function() {
    var m = angular.module('parkandride', [
        'ui.bootstrap',

        'templates-app',
        'templates-common',

        'ui.router',

        'filters',
        'featureToggle',

        'parkandride.hubList',

        'parkandride.dev'
    ]);

    m.constant('FEATURES_URL', 'assets/features.json');

    m.config(function myAppConfig($stateProvider, $urlRouterProvider, $httpProvider) {
        $urlRouterProvider.otherwise('/hubs');

        $httpProvider.interceptors.push(function($q) {
            return {
                responseError: function(rejection) {
                    alert(rejection.data.message);
                    return $q.reject(rejection);
                }
            };
        });
    });

    m.value("schema", { capacityTypes:[] });

    m.run(function run(schema, FacilityResource) {
        // Use the main applications run method to execute any code after services have been instantiated
        FacilityResource.getCapacityTypes().then(function(types) {
            schema.capacityTypes = types;
        });
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
