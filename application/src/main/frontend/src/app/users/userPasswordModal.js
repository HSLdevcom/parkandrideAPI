(function() {
    var m = angular.module('parkandride.users.userPasswordModal', [
        'ui.bootstrap',
        'ui.router',
        'parkandride.auth',
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

    m.controller('UserPasswordModalCtrl', function($scope, $modalInstance, permit, Permission, user, UserResource) {
        var vm = this;
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
                UserResource.updatePassword(user).then(handleSubmitSuccess, handleSubmitReject);
            }
            // TODO notify user on pass conflict
        }

        function cancel() {
            $modalInstance.dismiss();
        }

        function handleSubmitSuccess(success) {
            $modalInstance.close(success);
        }

        function handleSubmitReject(rejection) {
            if (rejection.status == 400 && rejection.data.violations) {
                $scope.violations = rejection.data.violations;
            }
        }
    });
})();
