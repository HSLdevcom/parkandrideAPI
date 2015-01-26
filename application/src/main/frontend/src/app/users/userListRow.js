(function() {
    var m = angular.module('parkandride.users.userListRow', [
        'ui.router',
        'parkandride.users.userModal',
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

    m.controller('UserListRowCtrl', function($state, userModal) {
        var vm = this;
        vm.openModal = openModal;

        function openModal() {
            userModal.open(vm.user).result.then(function() { $state.reload(); });
        }
    });
})();
