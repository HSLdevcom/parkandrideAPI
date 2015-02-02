(function() {
    var m = angular.module('parkandride.users.userPasswordModal', [
        'ui.bootstrap',
        'ui.router',
        'parkandride.auth',
        'parkandride.modalUtil',
        'parkandride.UserResource',
        'parkandride.OperatorResource',
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

    m.controller('UserPasswordModalCtrl', function($scope, $modalInstance, permit, Permission, user, UserResource, modalUtilFactory) {
        var vm = this;
        var modalUtil = modalUtilFactory($scope, $modalInstance);

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
                $scope.violations = [{
                    path: "newPassword",
                    type: "PasswordConfirmationMismatch"
                }];
            }
        }

        function cancel() {
            $modalInstance.dismiss();
        }

    });
})();
