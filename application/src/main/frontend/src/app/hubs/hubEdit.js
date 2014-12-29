(function() {
    var m = angular.module('parkandride.hubEdit', [
        'ui.router',
        'parkandride.hubMap',
        'parkandride.capacities',
        'parkandride.layout',
        'parkandride.HubResource',
        'parkandride.FacilityResource',
        'parkandride.address',
        'showErrors'
    ]);

    var stateConfigBase = {
        parent: 'root',
        views: {
            "main": {
                controller: 'HubEditCtrl as editCtrl',
                templateUrl: 'hubs/hubEdit.tpl.html'
            }
        }
    };

    m.config(function($stateProvider) {
        $stateProvider.state('hub-create', _.assign({
            url: '/hubs/create',
            data: { pageTitle: 'Create Hub' },
            resolve: {
                hub: function(HubResource) { return HubResource.newHub(); }
            }
        }, stateConfigBase));

        $stateProvider.state('hub-edit', _.assign({
            url: '/hubs/edit/:id',
            data: { pageTitle: 'Edit Hub' },
            resolve: {
                hub: function($stateParams, HubResource) { return HubResource.getHub($stateParams.id); }
            }
        }, stateConfigBase));
    });

    m.controller('HubEditCtrl', function ($scope, $state, HubResource, FacilityResource, hub) {
        var self = this;
        $scope.common.translationPrefix = "hubs";
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
