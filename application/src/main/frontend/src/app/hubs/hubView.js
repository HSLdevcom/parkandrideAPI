(function() {
    var m = angular.module('parkandride.hubView', [
        'ui.router',
        'parkandride.hubMap',
        'parkandride.facilitiesTable',
        'parkandride.capacities',
        'parkandride.address',
        'parkandride.FacilityResource',
        'parkandride.HubResource',
        'parkandride.multilingual'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('hub-view', { // dot notation in ui-router indicates nested ui-view
            parent: 'hubstab',
            url: '/hubs/view/:id',
            views: {
                "main": {
                    controller: 'HubViewCtrl as viewCtrl',
                    templateUrl: 'hubs/hubView.tpl.html',
                    resolve: {
                        hub: function($stateParams, HubResource) {
                            return HubResource.getHub($stateParams.id);
                        },
                        summary: function(hub, FacilityResource) {
                            return FacilityResource.summarizeFacilities(hub.facilityIds);
                        },
                        facilities: function(hub, FacilityResource) {
                            return FacilityResource.loadFacilities(hub.facilityIds);
                        }
                    }
                }
            },
            data: { pageTitle: 'View Hub' }
        });
    });

    m.controller('HubViewCtrl', function($scope, hub, summary, facilities) {
        this.hub = hub;
        this.summary = summary;
        this.facilities = facilities;
        this.hasFacilities = function() {
          return _.keys(facilities).length !== 0;
        };
        this.hasCapacities = function() {
          return _.keys(summary.capacities).length !== 0;
        };
        this.hasAddress = function() {
                return hub.address && hub.address.streetAddress || hub.address.postalCode || hub.address.city;
        };
    });

    m.directive('hubViewNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubViewNavi.tpl.html'
        };
    });

})();
