(function() {
    var m = angular.module('parkandride.facilities', [
        'ui.router',
        'pascalprecht.translate',

        'parkandride.services.facilities',
        'parkandride.services.translations',

        'facilities.create',
        'facilities.view'
    ]);

    m.config(function config($stateProvider) {
            $stateProvider.state('facilities', {
                url: '/facilities',
                views: {
                    "main": {
                        controller: 'FacilitiesCtrl',
                        templateUrl: 'facilities/facilities.tpl.html'
                    }
                },
                data: { pageTitle: 'Facilities' }
            });
        });

    m.controller('FacilitiesCtrl', FacilitiesController);
    function FacilitiesController(FacilityService, $translate) {
        var origThis = this;
        this.list = [];

        FacilityService.getFacilities().then(function(data){
            origThis.list = data;
        });

        // TODO this should be done in directive (as it qualifies to output formatting)?
        this.translatedCapacities = function(facility) {
            return _.values($translate.instant(_.map(Object.keys(facility.capacities), function (capacityType) {
                return "facilities.common.capacity." + capacityType;
            })));
        };
    }
})();