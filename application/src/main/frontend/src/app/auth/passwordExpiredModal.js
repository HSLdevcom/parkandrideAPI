// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.auth.passwordExpiredModal', [
        'ui.bootstrap',
        'ui.router',
        'parkandride.auth',
        'parkandride.modalUtil',
        'parkandride.UserResource',
        'parkandride.OperatorResource',
        'parkandride.components.violations',
        'parkandride.i18n'
    ]);

    m.factory('passwordExpiredModal', function($modal) {
        return {
            open: function(login) {
                return $modal.open({
                    templateUrl: 'auth/passwordExpiredModal.tpl.html',
                    controller: 'PasswordExpiredModalCtrl as ctrl',
                    resolve: {
                        user: function () {
                            var user = {};
                            user.id = login.userId;
                            user.permissions = login.permissions;
                            return user;
                        }
                    },
                    backdrop: 'static'
                });
            }
        };
    });

    m.controller('PasswordExpiredModalCtrl', function($scope, $modalInstance, permit, Permission, user, UserResource, modalUtilFactory, violationsManager) {
        var vm = this;
        vm.context = "users";
        var modalUtil = modalUtilFactory($scope, vm.context, $modalInstance);

        vm.user = user;
        vm.permitSave = permitSave;
        vm.save = save;
        vm.cancel = cancel;


        function permitSave() {
            return permit(Permission.USER_UPDATE);
        }

        function save(form) {
            if (vm.newPassword === vm.newPassword2) {
                vm.user.password = vm.newPassword;
                modalUtil.submit(function() { return UserResource.updatePassword(user); });
            } else {
                violationsManager.setViolations(vm.context, [{
                    path: "newPassword",
                    type: "PasswordConfirmationMismatch"
                }]);
            }
        }

        function cancel() {
            $modalInstance.dismiss();
        }

    });
})();
