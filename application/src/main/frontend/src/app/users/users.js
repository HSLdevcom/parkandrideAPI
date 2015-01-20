(function() {
    var m = angular.module('parkandride.users', [
        'ui.router',
        'parkandride.UserResource',
        'parkandride.layout',
        'parkandride.util',
        'showErrors'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('user-list', {
            parent: 'userstab',
            url: '/users',
            views: {
                "main": {
                    controller: 'UserListCtrl as ctrl',
                    templateUrl: 'users/userList.tpl.html'
                }
            },
            data: { pageTitle: 'Users' },
            resolve: {
                users: function(UserResource) {
                    return UserResource.listUsers();
                }
            }
        });
    })
    .controller('UserListCtrl', function(users) {
        var vm = this;
        vm.users = users.results;
        vm.create = create;
        vm.edit = edit;

        function create() {}
        function edit() {}
    });

    m.directive('userListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'users/userListNavi.tpl.html'
        };
    });

})();
