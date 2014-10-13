(function() {
    var m = angular.module('parkandride.facilityList', [
        'ui.router',
        'parkandride.i18n',
        'parkandride.FacilityResource',
        'parkandride.facilityEdit',
        'parkandride.facilityView'
    ]);

    m.config(function config($stateProvider) {
            $stateProvider.state('facility-list', {
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

        FacilityResource.listFacilities().then(function(data){
            origThis.list = data;
        });

        // TODO this should be done in directive (as it qualifies to output formatting)?
        this.translatedCapacities = function(facility) {
            // FIXME: Refactor translate into i18n module!
            return _.values($translate.instant(_.map(Object.keys(facility.capacities), function (capacityType) {
                return "facilities.capacity." + capacityType;
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