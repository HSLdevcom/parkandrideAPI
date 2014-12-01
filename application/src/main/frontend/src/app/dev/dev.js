(function() {
    var m = angular.module('parkandride.dev', [
        'ui.router',
        'parkandride.FacilityResource',
        'parkandride.HubResource'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('dev-page', {
            parent: 'root',
            url: '/dev',
            views: {
                "main": {
                    controller: 'DevCtrl as devCtrl',
                    templateUrl: 'dev/dev.tpl.html'
                }
            },
            data: { pageTitle: 'Development Atom Bomb' }
        });
    });

    m.factory('DevService', function($http, $q) {
        var api = {
            resetFacilities: function(facilities) {
                var promise = $http['delete']('/dev-api/facilities');
                if (facilities) {
                    return promise.then(function() {
                        return $http.put('/dev-api/facilities', facilities).then(function(response){
                            return response.data;
                        });
                    });
                } else {
                    return promise;
                }
            },
            resetHubs: function(hubs) {
                var promise = $http['delete']('/dev-api/hubs');
                if (hubs) {
                    return promise.then(function() {
                        return $http.put('/dev-api/hubs', hubs).then(function(response){
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

    m.controller('DevCtrl', function(DevService, FacilityResource, HubResource, $q, $scope) {
        this.resetAll = function() {
            DevService.resetAll().then(function() {
                $scope.successMessage = "Reset all OK!";
            });
        };
        this.saveCurrentState = function() {
            $q.all([FacilityResource.listFacilities(), HubResource.listHubs()]).then(function(allResults) {
                localStorage.setItem("facilities", angular.toJson(allResults[0]));
                localStorage.setItem("hubs", angular.toJson(allResults[1]));
                $scope.successMessage = "Current facilities and hubs saved!";
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
                $scope.successMessage = "Reset to previous state OK!";
            });
        };
    });
})();