(function() {
    var m = angular.module('parkandride.hubEdit', [
        'ui.router',
        'parkandride.hubMap',
        'parkandride.capacities',
        'parkandride.layout',
        'parkandride.HubResource',
        'parkandride.FacilityResource',
        'parkandride.address',
        'parkandride.submitUtil',
        'showErrors'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('hub-create', { // dot notation in ui-router indicates nested ui-view
            parent: 'hubstab',
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
            parent: 'hubstab',
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

    m.controller('HubEditCtrl', function ($scope, $state, HubResource, FacilityResource, hub, submitUtilFactory) {
        var self = this;
        self.context = "hubs";
        var submitUtil = submitUtilFactory($scope, self.context);

        self.hub = hub;
        self.facilities = [];
        self.hasFacilities = function() {
          return _.keys(self.facilities).length !== 0;
        };
        self.save = function (form) {
            submitUtil.validateAndSubmit(
                form,
                function () { return HubResource.save(self.hub); },
                function (id) { return $state.go('hub-view', { "id": id }); }
            );
        };
    });

    m.directive('hubEditNavi', function () {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubEditNavi.tpl.html'
        };
    });

})();
