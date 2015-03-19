// Copyright © 2015 HSL

(function() {
    var m = angular.module('parkandride.users.userList', [
        'parkandride.users.userListRow',
        'parkandride.i18n'
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

    m.controller('UserListCtrl', function() {});
})();
