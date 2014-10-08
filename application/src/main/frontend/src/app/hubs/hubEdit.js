(function() {
    var m = angular.module('parkandride.hubEdit', [
        'ui.router',
        'parkandride.hubMap',
        'parkandride.HubResource',
        'parkandride.FacilityResource',
        'ngTagsInput'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('hub-create', { // dot notation in ui-router indicates nested ui-view
            url: '/hubs/create',
            views: {
                "main": {
                    controller: 'HubEditCtrl as editCtrl',
                    templateUrl: 'hubs/hubEdit.tpl.html'
                }
            },
            data: { pageTitle: 'Create Hub' },
            resolve: {
                hub: function(HubResource) {
                    return HubResource.newHub();
                }
            }
        });
        $stateProvider.state('hub-edit', { // dot notation in ui-router indicates nested ui-view
            url: '/hubs/edit/:id',
            views: {
                "main": {
                    controller: 'HubEditCtrl as editCtrl',
                    templateUrl: 'hubs/hubEdit.tpl.html'
                }
            },
            data: { pageTitle: 'Edit Hub' },
            resolve: {
                hub: function($stateParams, HubResource) {
                    return HubResource.getHub($stateParams.id);
                }
            }
        });
    });

    m.controller('HubEditCtrl', function($state, HubResource, hub) {

        this.hub = hub;

        this.saveHub = function() {
            HubResource.save(this.hub).then(function(id){
                $state.go('hub-view', { "id": id });
            });
        };
    });

    m.directive('hubEditNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubEditNavi.tpl.html'
        };
    });
})();

