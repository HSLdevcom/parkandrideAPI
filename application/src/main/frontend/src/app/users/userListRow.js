(function() {
    var m = angular.module('parkandride.users.userListRow', [
        'ui.router',
        'parkandride.auth',
        'parkandride.users.userModal',
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

    m.controller('UserListRowCtrl', function($state, $translate, UserResource, userModal, userPasswordModal, permit, Permission, Session) {
        var vm = this;
        vm.openModal = openModal;
        vm.isApi = isApi;
        vm.updateSecret = updateSecret;
        vm.permitUpdateSecret = permitUpdateSecret;
        vm.remove = remove;
        vm.permitRemove = permitRemove;

        function openModal() {
            userModal.open(vm.user).result.then(function() { $state.reload(); });
        }

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
                    title: $translate.instant('users.listActions.updateToken.successMessage'),
                    text: newToken,
                    type: "success",
                    width: 650
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
                title: $translate.instant('users.listActions.remove.confirmTitle'),
                text: $translate.instant('users.listActions.remove.confirmText'),
                type: "warning",
                showCancelButton: true,
                confirmButtonText: $translate.instant('users.listActions.remove.confirmButtonText'),
                cancelButtonText: $translate.instant('common.action.cancel'),
                closeOnConfirm: true
            }, function() {
                UserResource.remove(vm.user).then(function(){
                    $state.reload();
                });
            });
        }
    });
})();
