(function() {
    var m = angular.module('parkandride.hubList', [
        'ui.router',
        'parkandride.capacities',
        'parkandride.HubResource',
        'parkandride.hubEdit',
        'parkandride.hubView'
    ]);

    m.config(function config($stateProvider) {
            $stateProvider.state('hub-list', {
                url: '/hubs',
                views: {
                    "main": {
                        controller: 'HubsCtrl as hubsCtrl',
                        templateUrl: 'hubs/hubList.tpl.html'
                    }
                },
                data: { pageTitle: 'Hubs' },
                resolve: {
                    hubs: function(HubResource) {
                        return HubResource.listHubs();
                    },
                    facilities: function(FacilityResource) {
                        return FacilityResource.listFacilities().then(function(facilities) {
                            return _.indexBy(facilities, "id");
                        });
                    }
                }
            });
        });

    m.controller('HubsCtrl', HubsController);
    function HubsController(HubResource, hubs, facilities) {
        this.hubs = hubs;
        var attachedFacilityIds = _.sortBy(_.flatten(hubs, "facilityIds"));
        this.getFacilities = function(hub) {
            return _.map(hub.facilityIds, function(facilityId) {
               return facilities[facilityId];
            });
        };
        this.getUnattachedFacilities = function() {
            return _.filter(facilities, function(facility) {
                return _.indexOf(attachedFacilityIds, facility.id, true) < 0;
            });
        };
    }

    m.directive('hubListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubListNavi.tpl.html'
        };
    });

})();
