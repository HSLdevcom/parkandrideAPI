(function() {
    var m = angular.module('parkandride.users.userList', [
        'ui.router',
        'parkandride.users.userModal',
        'parkandride.i18n',
        'showErrors'
    ]);

    m.directive('userList', function() {
        return {
            restrict: 'E',
            scope: {
                users: '='
            },
            bindToController: true,
            controller: 'UserListCtrl',
            controllerAs: 'ctrl',
            templateUrl: 'users/userList.tpl.html',
            replace: true
        };
    });

    m.controller('UserListCtrl', function($state, userModal) {
        var vm = this;
        vm.openModal = openModal;

        console.log("users ", vm.users);

        function openModal(user) {
            userModal.open(user).result.then(function() { $state.reload(); });
        }
    });
})();
