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

    m.controller('UserListRowCtrl', function($state, $translate, UserResource, userModal, userPasswordModal, permit, Permission) {
        var vm = this;
        vm.openModal = openModal;
        vm.isApi = isApi;
        vm.updateSecret = updateSecret;
        vm.permitUpdateUser = permitUpdateUser;

        function permitUpdateUser() {
            return permit(Permission.USER_UPDATE);
        }

        function openModal() {
            userModal.open(vm.user).result.then(function() { $state.reload(); });
        }

        function isApi() { return vm.user.role === 'OPERATOR_API'; }

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
    });
})();
