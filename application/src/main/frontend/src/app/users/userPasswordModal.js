// Copyright © 2015 HSL

(function() {
    var m = angular.module('parkandride.users.userPasswordModal', [
        'ui.bootstrap',
        'ui.router',
        'parkandride.auth',
        'parkandride.modalUtil',
        'parkandride.UserResource',
        'parkandride.OperatorResource',
        'parkandride.components.violations',
        'parkandride.i18n'
    ]);

    m.factory('userPasswordModal', function($modal) {
        return {
            open: function(user) {
                return $modal.open({
                    templateUrl: 'users/userPasswordModal.tpl.html',
                    controller: 'UserPasswordModalCtrl as ctrl',
                    resolve: {
                        user: function () {
                            return user;
                        }
                    },
                    backdrop: 'static'
                });
            }
        };
    });

    m.controller('UserPasswordModalCtrl', function($scope, $modalInstance, permit, Permission, user, UserResource, modalUtilFactory, violationsManager) {
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
