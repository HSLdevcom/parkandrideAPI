(function() {
    var m = angular.module('parkandride.auth', []);

    m.factory('Session', function() {
        return {
            set: function(login) {
                sessionStorage.authToken = login.token;
                sessionStorage.authUsername = login.username;
                sessionStorage.authRole = login.role;
            },
            remove: function() {
                delete sessionStorage.authToken;
                delete sessionStorage.authUsername;
                delete sessionStorage.authRole;
            },
            get: function() {
                if (sessionStorage.authToken) {
                    return {
                        token: sessionStorage.authToken,
                        username: sessionStorage.authUsername,
                        role: sessionStorage.authRole
                    };
                } else {
                    return null;
                }
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
