(function() {
    var m = angular.module('parkandride', [
        'ui.bootstrap',

        'templates-app',
        'templates-common',

        'ui.router',

        'filters',

        'parkandride.i18n',
        'parkandride.facilityList',
        'parkandride.hubList'
    ]);

    m.config(function myAppConfig($stateProvider, $urlRouterProvider, $httpProvider) {
        $urlRouterProvider.otherwise('/facilities');

        $httpProvider.interceptors.push(function($q, $translate) {
            return {
                responseError: function(rejection) {
                    swal({
                        title: $translate.instant('error.unexpected.title'),
                        text: rejection.data.message,
                        confirmButtonText: $translate.instant('error.unexpected.buttonText')
                    });
                    return $q.reject(rejection);
                }
            };
        });
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
