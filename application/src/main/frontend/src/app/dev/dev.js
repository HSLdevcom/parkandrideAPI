(function() {
    var m = angular.module('parkandride.dev', [
        'ui.router'
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
        function reset(endpoint, data) {
            var url = 'dev-api/' + endpoint;
            var promise = $http['delete'](url);
            if (data) {
                return promise.then(function() {
                    return $http.put(url, data).then(function(response){
                        return response.data;
                    });
                });
            }
            return promise;
        }
        
        var api = {
            resetFacilities: function(facilities) {
                return reset('facilities', facilities);
            },
            resetHubs: function(hubs) {
                return reset('hubs', hubs);
            },
            resetContacts: function(contacts) {
                return reset('contacts', contacts);
            },
            resetAll: function(facilities, hubs, contacts) {
                return $q.all([api.resetFacilities(facilities), api.resetContacts(contacts), api.resetHubs(hubs)]);
            }
        };
        return api;
    });

    m.controller('DevCtrl', function(DevService, $http, $q, $scope) {
        this.resetAll = function() {
            DevService.resetAll().then(function() {
                $scope.successMessage = "Reset all OK!";
            });
        };
        this.saveCurrentState = function() {
            function list(entity) {
                return $http.get('api/v1/' + entity).then(function(response) {
                    return response.data.results;
                });
            }

            $q.all([list('facilities'), list('hubs'), list('contacts')]).then(function(allResults) {
                localStorage.setItem("facilities", angular.toJson(allResults[0]));
                localStorage.setItem("hubs", angular.toJson(allResults[1]));
                localStorage.setItem("contacts", angular.toJson(allResults[2]));
                $scope.successMessage = "Current state saved!";
            });
        };
        this.revertToSavedState = function() {
            function fromLocalStorage(name) {
                var stored = localStorage.getItem(name);
                return stored ? angular.fromJson(stored) : stored;
            }

            DevService.resetAll(fromLocalStorage('facilities'), fromLocalStorage('hubs'), fromLocalStorage('contacts')).then(function() {
                $scope.successMessage = "Reset to previous state OK!";
            });
        };
    });
})();