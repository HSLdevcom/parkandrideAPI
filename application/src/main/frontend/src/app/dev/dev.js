(function() {
    var urlPrefix = {
        api : "api/v1/",
        devApi : "dev-api/"
    };

    var m = angular.module('parkandride.dev', [
        'ui.router'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('dev-page', {
            parent: 'devtab',
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
        function insert(endpoint, data) {
            if (data) {
                return $http.put(urlPrefix.devApi + endpoint, data).then(function(response){
                    return response.data;
                });
            }
            var defer = $q.defer();
            defer.resolve('no data');
            return defer;
        }

        function remove(endpoint) {
            return $http['delete'](urlPrefix.devApi + endpoint);
        }

        function reset(endpoint, data) {
            return remove(endpoint).then(function(){
                return insert(endpoint, data);
            });
        }

        var api = {
            resetAll: function(facilities, hubs, contacts) {
                return remove('facilities')
                    .then(function() { return reset('contacts', contacts); })
                    .then(function() { return $q.all([ insert('facilities', facilities), reset('hubs', hubs)]);
                });
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
                return $http.get(urlPrefix.api + entity).then(function(response) {
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