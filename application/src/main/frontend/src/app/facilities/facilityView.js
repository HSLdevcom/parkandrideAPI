(function() {
    var m = angular.module('parkandride.facilityView', [
        'ui.router',
        'parkandride.FacilityResource'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facilities-view', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/view/:id', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilityViewCtrl as viewCtrl',
                    templateUrl: 'facilities/facilityView.tpl.html',
                    resolve: {
                        facility: function($stateParams, FacilityResource) {
                            return FacilityResource.getFacility($stateParams.id);
                        }
                    }
                }
            },
            data: { pageTitle: 'View Facility' }
        });
    });

    m.controller('FacilityViewCtrl', ViewController);
    function ViewController(facility) {
        this.facility = facility;
    }

    m.directive('viewNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityViewNavi.tpl.html'
        };
    });
})();