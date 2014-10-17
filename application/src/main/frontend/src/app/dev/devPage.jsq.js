(function() {
    var m = angular.module('parkandride.dev', [
        'ui.router',
        'parkandride.FacilityResource',
        'parkandride.HubResource'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('dev-page', {
            url: '/dev',
            views: {
                "main": {
                    controller: 'DevPageCtrl as devCtrl',
                    templateUrl: 'dev/devPage.tpl.html'
                }
            },
            data: { pageTitle: 'Development Atom Bomb' }
        });
    });

    m.factory('DevService', function($http, $q) {
        var api = {
            resetFacilities: function(facilities) {
                var promise = $http['delete']('/test/facilities');
                if (facilities) {
                    return promise.then(function() {
                        return $http.put('/test/facilities', facilities).then(function(response){
                            return response.data;
                        });
                    });
                } else {
                    return promise;
                }
            },
            resetHubs: function(hubs) {
                var promise = $http['delete']('/test/hubs');
                if (hubs) {
                    return promise.then(function() {
                        return $http.put('/test/hubs', hubs).then(function(response){
                            return response.data;
                        });
                    });
                } else {
                    return promise;
                }
            },
            resetAll: function(facilities, hubs) {
                return $q.all([api.resetFacilities(facilities), api.resetHubs(hubs)]);
            }
        };
        return api;
    });

    m.controller('DevPageCtrl', function(DevService, FacilityResource, HubResource, $q) {
        this.resetAll = function() {
            DevService.resetAll().then(function() {
                alert("Reset all OK!");
            });
        };
        this.saveCurrentState = function() {
            $q.all([FacilityResource.listFacilities(), HubResource.listHubs()]).then(function(allResults) {
                localStorage.setItem("facilities", angular.toJson(allResults[0]));
                localStorage.setItem("hubs", angular.toJson(allResults[1]));
                alert("Current facilities and hubs saved!");
            });
        };
        this.revertToSavedState = function() {
            var facilities = localStorage.getItem('facilities');
            if (facilities) {
                facilities = angular.fromJson(facilities);
            }
            var hubs = localStorage.getItem('hubs');
            if (hubs) {
                hubs = angular.fromJson(hubs);
            }
            DevService.resetAll(facilities, hubs).then(function() {
                alert("Reset to previous state OK!");
            });
        };
    });
})();