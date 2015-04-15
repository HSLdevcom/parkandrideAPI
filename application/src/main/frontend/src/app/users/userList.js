// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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
