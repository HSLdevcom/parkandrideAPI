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
                },
                operators: function(OperatorResource) {
                    return OperatorResource.listOperators();
                }
            }
        });
    });
    m.controller('UserListCtrl', function($state, users, operators, userModal) {
        var vm = this;
        vm.users = users.results;
        vm.openModal = openModal;

        initialize();

        function initialize() {
            var operatorsById = _.indexBy(operators.results, "id");
            _.forEach(vm.users, function(user) {
                var operator = operatorsById[user.operatorId];
                user.operatorNameFi = operator ? operator.name.fi : "";
            });
        }

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
    m.controller('UserModalCtrl', function($scope, $modalInstance, $translate, schema, user, EVENTS, UserResource) {
        var vm = this;
        vm.titleKey = 'users.action.' + (user ? 'edit' : 'new');
        vm.user = user;
        vm.save = save;
        vm.cancel = cancel;
        vm.roles = translatedEnumValues("roles", schema.roles);

        function save(form) {
            validateAndSubmit(form, function() {
                UserResource.save(vm.user).then(handleSubmitSuccess, handleSubmitReject);
            });
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

        function handleSubmitSuccess(success) {
            $modalInstance.close(success);
        }

        function handleSubmitReject(rejection) {
            if (rejection.status == 400 && rejection.data.violations) {
                $scope.violations = rejection.data.violations;
            }
        }

        function validateAndSubmit(form, saveFn) {
            $scope.$broadcast(EVENTS.showErrorsCheckValidity);
            if (form.$valid) {
                saveFn();
            } else {
                $scope.violations = [{
                    path: "",
                    type: "BasicRequirements"
                }];
            }
        }
    });

})();
