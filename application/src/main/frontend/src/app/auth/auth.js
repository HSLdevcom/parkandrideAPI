(function() {
    var m = angular.module('parkandride.auth', []);

    m.factory('Session', function() {
        var storageKey = "login";
        var loginCache;
        return {
            set: function(login) {
                loginCache = login;
                sessionStorage.setItem(storageKey, angular.toJson(login));
            },
            remove: function() {
                loginCache = null;
                sessionStorage.clear();
            },
            get: function() {
                if (loginCache) {
                    return loginCache;
                }
                var loginData = sessionStorage.getItem(storageKey);
                if (loginData) {
                    return angular.fromJson(loginData);
                }
                return null;
            }
        };
    });

    m.controller('LoginController', function($scope, $modalInstance, $http, Session) {
        $scope.credentials = {
            username: "",
            password: ""
        };
        $scope.login = function(credentials) {
            $scope.loginError = false;
            $http.post("internal/login", credentials, {"skipDefaultViolationsHandling": true}).then(
                function(result) {
                    Session.set(result.data);
                    $modalInstance.close(result.data);
                },
                function(rejection) {
                    $scope.loginError = true;
                }
            );
        };
    });

    m.factory("loginPrompt", function($modal) {
        return function() {
            var modalInstance = $modal.open({
                templateUrl: 'auth/login.tpl.html',
                controller: 'LoginController'
            });
            return modalInstance.result;
        };
    });

})();
