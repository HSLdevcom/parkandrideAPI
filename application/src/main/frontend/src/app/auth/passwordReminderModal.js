// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.auth.passwordReminderModal', [
        'ui.bootstrap',
        'ui.router',
        'parkandride.auth',
        'parkandride.i18n'
    ]);

    m.factory('passwordReminderModal', function($modal) {
        return {
            open: function(passwordRemainingDays) {
                return $modal.open({
                    templateUrl: 'auth/passwordReminderModal.tpl.html',
                    controller: 'PasswordReminderModalCtrl as ctrl',
                    resolve: {
                        passwordRemainingDays: function () {
                            return passwordRemainingDays;
                        }
                    },
                    backdrop: 'static'
                });
            }
        };
    });

    m.controller('PasswordReminderModalCtrl', function($scope, $modalInstance, passwordRemainingDays) {
        var vm = this;
        vm.passwordRemainingDays = passwordRemainingDays;
        vm.ok = ok;
        
        function ok() {
            $modalInstance.dismiss();
        }
    });
})();
