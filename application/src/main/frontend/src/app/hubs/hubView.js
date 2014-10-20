(function() {
    var m = angular.module('parkandride.hubView', [
        'ui.router',
        'parkandride.hubMap',
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
                        }
                    }
                }
            },
            data: { pageTitle: 'View Hub' }
        });
    });

    m.controller('HubViewCtrl', function($scope, hub, summary, FacilityResource) {
        this.hub = hub;
        this.summary = summary;
    });

    m.directive('hubViewNaviAbove', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubViewNaviAbove.tpl.html'
        };
    });

    m.directive('hubViewNaviBelow', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubViewNaviBelow.tpl.html'
        };
    });
})();
