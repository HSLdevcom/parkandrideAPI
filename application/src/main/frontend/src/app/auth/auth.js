// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.auth', [
        'parkandride.auth.passwordReminderModal',
        'parkandride.auth.passwordExpiredModal'
    ]);

    m.factory('Session', function() {
        var storageKey = "login";
        var loginCache;
        return {
            set: function(user) {
                if (_.isArray(user.permissions)) {
                    user.permissions = _.reduce(
                        user.permissions,
                        function(obj, perm) {
                            obj[perm] = true;
                            return obj;
                        },
                        {}
                    );
                }
                loginCache = user;
                sessionStorage.setItem(storageKey, angular.toJson(user));
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
                    loginCache = angular.fromJson(loginData);
                }
                return loginCache;
            }
        };
    });

    m.value('Permission', {
        ALL_OPERATORS: 'ALL_OPERATORS',
        FACILITY_CREATE: 'FACILITY_CREATE',
        FACILITY_UPDATE: 'FACILITY_UPDATE',
        OPERATOR_CREATE: 'OPERATOR_CREATE',
        OPERATOR_UPDATE: 'OPERATOR_UPDATE',
        CONTACT_CREATE: 'CONTACT_CREATE',
        CONTACT_UPDATE: 'CONTACT_UPDATE',
        USER_CREATE: 'USER_CREATE',
        USER_UPDATE: 'USER_UPDATE',
        USER_VIEW: 'USER_VIEW',
        FACILITY_STATUS_UPDATE: 'FACILITY_STATUS_UPDATE',
        HUB_CREATE: 'HUB_CREATE',
        HUB_UPDATE: 'HUB_UPDATE',
        REPORT_GENERATE: 'REPORT_GENERATE'
    });

    m.factory('permit', function(Session, Permission) {
       return function(permissions, operatorId) {
           if (!_.isArray(permissions)) {
               permissions = [ permissions ];
           }

           var user = Session.get();
           if (user) {
               for (var i=0; i < permissions.length; i++) {
                   var permission = permissions[i];
                   if (!Permission[permission]) {
                       throw "Unknown permission: " + permission;
                   }

                   if (user.permissions[permission]) {
                       if (arguments.length < 2) {
                           return true;
                       }
                       else if (user.permissions[Permission.ALL_OPERATORS]) {
                           return true;
                       }
                       else if (operatorId === user.operatorId) {
                           return true;
                       }
                   }
               }
           }
           return false;
       };
    });

    m.controller('LoginController', function($scope, $modalInstance, $http, Session, $state, passwordReminderModal, passwordExpiredModal, $q) {
        $scope.credentials = {
            username: "",
            password: ""
        };
        $scope.login = function(credentials) {
            $scope.loginError = false;
            $http.post("internal/login", credentials, {"skipDefaultViolationsHandling": true}).then(
                function(result) {
                    var promise = $q(function (resolve, reject) {
                        var userData = result.data;
                        if (userData.passwordExpireInDays < 0) {
                            passwordExpiredModal.open(userData).result.then(
                                function () { resolve(userData); },
                                function () { reject(userData); }
                            );
                        } else {
                            if (userData.passwordExpireInDays > 0) {
                                passwordReminderModal.open(userData.passwordExpireInDays).result.then(function () { });
                            }
                            resolve(userData);
                        }
                    });
                    promise.then($modalInstance.close.bind($modalInstance), $modalInstance.close.bind($modalInstance));
                    promise.then(Session.set.bind(Session), Session.remove.bind(Session));
                    return promise;
                },
                function(rejection) {
                    $scope.loginError = true;
                }
            );
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });

    m.factory("loginPrompt", function ($uibModal) {
        return function() {
            var modalInstance = $uibModal.open({
                templateUrl: 'auth/login.tpl.html',
                controller: 'LoginController',
                backdrop: 'static'
            });
            return modalInstance.result;
        };
    });

})();
