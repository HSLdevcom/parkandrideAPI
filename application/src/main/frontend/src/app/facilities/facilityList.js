(function() {
    var m = angular.module('parkandride.facilityList', [
        'ui.router',
        'pascalprecht.translate',

        'parkandride.FacilityResource',
        'parkandride.i18n',

        'parkandride.facilityEdit',
        'parkandride.facilityView'
    ]);

    m.config(function config($stateProvider) {
            $stateProvider.state('facilities', {
                url: '/facilities',
                views: {
                    "main": {
                        controller: 'FacilitiesCtrl as fctrl',
                        templateUrl: 'facilities/facilityList.tpl.html'
                    }
                },
                data: { pageTitle: 'Facilities' }
            });
        });

    m.controller('FacilitiesCtrl', FacilitiesController);
    function FacilitiesController(FacilityResource, $translate) {
        var origThis = this;
        this.list = [];

        FacilityResource.getFacilities().then(function(data){
            origThis.list = data;
        });

        // TODO this should be done in directive (as it qualifies to output formatting)?
        this.translatedCapacities = function(facility) {
            return _.values($translate.instant(_.map(Object.keys(facility.capacities), function (capacityType) {
                return "facilities.common.capacity." + capacityType;
            })));
        };
    }

    m.directive('facilityListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityListNavi.tpl.html'
        };
    });

})();