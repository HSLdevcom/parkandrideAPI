angular.module( 'ngBoilerplate', [
  'templates-app',
  'templates-common',
  'ngBoilerplate.home',
  'ngBoilerplate.about',
  'ngBoilerplate.facilities',
  'ui.router'
])

.config( function myAppConfig ( $stateProvider, $urlRouterProvider ) {
//  $urlRouterProvider.otherwise( '/home' );
  $urlRouterProvider.otherwise( '/facilities' );
})

.run( function run () {
  // Use the main applications run method to execute any code after services have been instantiated
})

.controller( 'AppCtrl', function AppCtrl ( $scope, $location ) {
  // This is a good place for logic not specific to the template or route, such as menu logic or page title wiring
  $scope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
    if ( angular.isDefined( toState.data.pageTitle ) ) {
      $scope.pageTitle = toState.data.pageTitle + ' | ngBoilerplate' ;
    }
  });
})

;

