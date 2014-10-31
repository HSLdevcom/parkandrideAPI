(function() {
    var m = angular.module('parkandride.facilityView', [
        'ui.router',
        'parkandride.facilityMap',
        'parkandride.capacities',
        'parkandride.layout',
        'parkandride.FacilityResource',
        'parkandride.layout'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facility-view', { // dot notation in ui-router indicates nested ui-view
            parent: 'root',
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
        this.hasCapacities = function() {
          return _.keys(facility.capacities).length !== 0;
        };
    }

    m.directive('facilityViewNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityViewNavi.tpl.html'
        };
    });

//    m.directive('myLink', function(){
//        return {
//            restrict: 'E',
//            transclude: true,
//            scope: {},
//            template: '<a ng-transclude></a>',
//            compile: function(element, attrs) {
//                element.find("a").attr("ui-sref", attrs.uiSref);
//            }
//        };
//    });
})();
