(function() {
    var m = angular.module('parkandride.users', [
        'ui.router',
        'parkandride.auth',
        'parkandride.operators',
        'parkandride.UserResource',
        'parkandride.layout',
        'parkandride.users.userModal',
        'showErrors'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('user-list', {
            parent: 'userstab',
            url: '/users',
            views: {
                "main": {
                    controller: 'UserListCtrl as ctrl',
                    templateUrl: 'users/users.tpl.html'
                }
            },
            data: { pageTitle: 'Users' },
            resolve: {
                users: function(UserResource) {
                    return UserResource.listUsers();
                },
                operatorsById: function(OperatorResource) {
                    return OperatorResource.listOperators().then(function(operators) {
                        return _.indexBy(operators.results, "id");
                    });
                }
            }
        });
    });
    m.controller('UserListCtrl', function($state, users, operatorsById, userModal) {
        var vm = this;
        vm.users = users.results;
        vm.openModal = openModal;

        initialize();

        function initialize() {
            _.forEach(vm.users, function(user) {
                var operator = operatorsById[user.operatorId];
                user.operatorNameFi = operator ? operator.name.fi : "";
            });
        }

        function openModal(user) {
            userModal.open(user).result.then(function() { $state.reload(); });
        }
    });

    m.directive('usersNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'users/usersNavi.tpl.html'
        };
    });

})();
