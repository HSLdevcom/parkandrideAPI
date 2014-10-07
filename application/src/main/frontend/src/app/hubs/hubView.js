(function() {
    var m = angular.module('parkandride.hubView', [
        'ui.router',
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
                        }
                    }
                }
            },
            data: { pageTitle: 'View Hub' }
        });
    });

    m.controller('HubViewCtrl', ViewController);
    function ViewController(hub) {
        this.hub = hub;
    }

    m.directive('hubViewNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubViewNavi.tpl.html'
        };
    });
})();