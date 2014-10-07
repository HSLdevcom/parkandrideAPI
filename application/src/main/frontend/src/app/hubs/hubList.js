(function() {
    var m = angular.module('parkandride.hubList', [
        'ui.router',
        'parkandride.i18n',
        'parkandride.HubResource',
        'parkandride.hubEdit',
        'parkandride.hubView'
    ]);

    m.config(function config($stateProvider) {
            $stateProvider.state('hub-list', {
                url: '/hubs',
                views: {
                    "main": {
                        controller: 'HubsCtrl as fctrl',
                        templateUrl: 'hubs/hubList.tpl.html'
                    }
                },
                data: { pageTitle: 'Hubs' }
            });
        });

    m.controller('HubsCtrl', HubsController);
    function HubsController(HubResource, $translate) {
        var self = this;
        self.list = [];

        HubResource.listHubs().then(function(data){
            self.list = data;
        });

    }

    m.directive('hubListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'hubs/hubListNavi.tpl.html'
        };
    });

})();