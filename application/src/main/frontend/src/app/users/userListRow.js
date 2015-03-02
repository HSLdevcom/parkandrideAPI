(function() {
    var m = angular.module('parkandride.users.userListRow', [
        'ui.router',
        'parkandride.auth',
        'parkandride.users.userPasswordModal',
        'parkandride.UserResource',
        'parkandride.i18n'
    ]);

    m.directive('userListRow', function() {
        return {
            restrict: 'A',
            scope: {
                user: '='
            },
            bindToController: true,
            controller: 'UserListRowCtrl',
            controllerAs: 'ctrl',
            templateUrl: 'users/userListRow.tpl.html'
        };
    });

    m.controller('UserListRowCtrl', function($state, $translate, UserResource, userPasswordModal, permit, Permission, Session) {
        var vm = this;
        vm.isApi = isApi;
        vm.updateSecret = updateSecret;
        vm.permitUpdateSecret = permitUpdateSecret;
        vm.remove = remove;
        vm.permitRemove = permitRemove;

        function isApi() { return vm.user.role === 'OPERATOR_API'; }

        function permitUpdateSecret() {
            return permit(Permission.USER_UPDATE);
        }

        function updateSecret() {
            if (isApi()) {
                updateToken();
            } else {
                updatePassword();
            }
        }

        function updateToken() {
            UserResource.resetToken(vm.user).then(function(newToken) {
                swal({
                    text: $translate.instant('users.listActions.updateToken.successMessage') + newToken,
                    width: 600,
                    confirmButtonColor: "#007AC9"
                });
            });
        }

        function updatePassword() {
            userPasswordModal.open(vm.user).result.then(function() { $state.reload(); });
        }

        function permitRemove() {
            return Session.get().username !== vm.user.username && permit(Permission.USER_UPDATE);
        }

        function remove() {
            swal({
                text: $translate.instant('users.listActions.remove.confirmText'),
                width: 400,
                showCancelButton: true,
                cancelButtonText: $translate.instant('common.action.cancel'),
                cancelButtonColor: "#BEE4F8",
                confirmButtonText: $translate.instant('users.listActions.remove.confirmButtonText'),
                confirmButtonColor: "#007AC9",
                closeOnConfirm: true
            }, function() {
                UserResource.remove(vm.user).then(function(){
                    $state.reload();
                });
            });
        }
    });
})();
