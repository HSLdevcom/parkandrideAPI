(function() {
    var m = angular.module('parkandride.hubEdit', [
        'ui.router',
        'parkandride.hubMap',
        'parkandride.capacities',
        'parkandride.layout',
        'parkandride.HubResource',
        'parkandride.FacilityResource',
        'ngTagsInput',
        'showErrors'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('hub-create', { // dot notation in ui-router indicates nested ui-view
            parent: 'root',
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
            parent: 'root',
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

    m.controller('HubEditCtrl', function ($scope, $state, HubResource, FacilityResource, hub) {
        var self = this;
        $scope.common.translationPrefix = "hubs.";
        self.hub = hub;
        self.facilities = [];
        self.hasFacilities = function() {
          return _.keys(self.facilities).length !== 0;
        };
        self.saveHub = function () {
            HubResource.save(self.hub).then(function (id) {
                $state.go('hub-view', { "id": id });
            });
        };
    });

    m.directive('hubEditNavi', function () {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubEditNavi.tpl.html'
        };
    });

})();
