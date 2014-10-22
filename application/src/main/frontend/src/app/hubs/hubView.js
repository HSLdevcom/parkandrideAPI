(function() {
    var m = angular.module('parkandride.hubView', [
        'ui.router',
        'parkandride.hubMap',
        'parkandride.facilitiesTable',
        'parkandride.capacities',
        'parkandride.FacilityResource',
        'parkandride.HubResource'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('hub-view', { // dot notation in ui-router indicates nested ui-view
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
    });

    m.directive('hubViewNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubViewNavi.tpl.html'
        };
    });

})();
