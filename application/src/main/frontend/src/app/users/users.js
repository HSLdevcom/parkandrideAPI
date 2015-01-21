(function() {
    var m = angular.module('parkandride.users', [
        'ui.router',
        'parkandride.operators',
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
    });
    m.controller('UserListCtrl', function($state, users, userModal) {
        var vm = this;
        vm.users = users.results;
        vm.openModal = openModal;

        function openModal(user) {
            userModal.open(user).result.then(function() { $state.reload(); });
        }
    });

    m.directive('userListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'users/userListNavi.tpl.html'
        };
    });

    m.factory('userModal', function($modal) {
        return {
            open: function(user) {
                return $modal.open({
                    templateUrl: 'users/userModal.tpl.html',
                    controller: 'UserModalCtrl as ctrl',
                    resolve: {
                        user: function () {
                            return _.cloneDeep(user);
                        }
                    },
                    backdrop: 'static'
                });
            }
        };
    });
    m.controller('UserModalCtrl', function($modalInstance, $translate, schema, user) {
        var vm = this;
        vm.titleKey = 'users.action.' + (user ? 'edit' : 'new');
        vm.user = user;
        vm.save = save;
        vm.cancel = cancel;
        vm.roles = translatedEnumValues("roles", schema.roles);

        function save(form) {
            $modalInstance.close();
        }

        function cancel() {
            $modalInstance.dismiss();
        }

        function translatedEnumValues(prefix, values) {
            return _.map(values, function(v) {
                return {
                    id: v,
                    label: $translate.instant(prefix + "." + v + ".label")
                };
            });
        }
    });

})();
