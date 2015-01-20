(function() {
    var m = angular.module('parkandride.users', [
        'ui.router',
        'parkandride.UserResource',
        'parkandride.layout',
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
    .controller('UserListCtrl', function(users, userModal) {
        var vm = this;
        vm.users = users.results;
        vm.openModal = userModal;
    });

    m.directive('userListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'users/userListNavi.tpl.html'
        };
    });

    m.factory('userModal', function($modal) {
        return function(user) {
            var modalInstance = $modal.open({
                templateUrl: 'users/userModal.tpl.html',
                controller: 'UserModalCtrl as ctrl',
                resolve: {
                    user: function () {
                        return _.cloneDeep(user);
                    }
                },
                backdrop: 'static'
            });
            return modalInstance.result;
        };
    })
    .controller('UserModalCtrl', function($modalInstance, user) {
        var vm = this;
        vm.titleKey = 'users.action.' + (user ? 'edit' : 'new');
        vm.user = user;
        vm.save = save;
        vm.cancel = cancel;

        function save(form) {}
        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    });

})();
