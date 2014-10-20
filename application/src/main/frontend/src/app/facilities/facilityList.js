(function() {
    var m = angular.module('parkandride.facilityList', [
        'ui.router',
        'parkandride.facilitiesTable',
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
    function FacilitiesController(FacilityResource) {
        var origThis = this;
        this.list = [];

        FacilityResource.listFacilities().then(function(data){
            origThis.list = data;
        });
    }

    m.directive('facilityListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityListNavi.tpl.html'
        };
    });

})();