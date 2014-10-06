(function() {
    var m = angular.module('facilities.view', [
        'ui.router',
        'parkandride.services.facilities'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facilities-view', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/view/:id', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'ViewCtrl',
                    templateUrl: 'facilities/view.tpl.html',
                    resolve: {
                        facility: function($stateParams, FacilityService) {
                            return FacilityService.getFacility($stateParams.id);
                        }
                    }
                }
            },
            data: { pageTitle: 'View Facility' }
        });
    });

    m.controller('ViewCtrl', ViewController);
    function ViewController($scope, facility) {
        $scope.facility = facility;
    }

    m.directive('viewNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/view-navi.tpl.html'
        };
    });
})();