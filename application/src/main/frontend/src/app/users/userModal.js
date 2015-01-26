(function() {
    var m = angular.module('parkandride.users.userModal', [
        'ui.bootstrap',
        'ui.router',
        'ui.select',
        'parkandride.auth',
        'parkandride.UserResource',
        'parkandride.OperatorResource',
        'parkandride.i18n',
        'showErrors'
    ]);

    m.factory('userModal', function($modal, OperatorResource) {
        return {
            open: function(user) {
                return $modal.open({
                    templateUrl: 'users/userModal.tpl.html',
                    controller: 'UserModalCtrl as ctrl',
                    resolve: {
                        user: function () {
                            return _.cloneDeep(user);
                        },
                        operators: function() {
                            return OperatorResource.listOperators();
                        }
                    },
                    backdrop: 'static'
                });
            }
        };
    });

    m.controller('UserModalCtrl', function($scope, $modalInstance, $translate, schema, user, EVENTS, UserResource, operators, Session) {
        var vm = this;
        var isNewUser = !user;

        vm.titleKey = 'users.action.' + (isNewUser ? 'new' : 'edit');
        vm.user = user || {};
        vm.save = save;
        vm.cancel = cancel;
        vm.roles = translatedEnumValues("roles", schema.roles);
        vm.operators = operators.results;
        vm.onOperatorSelect = onOperatorSelect;
        vm.onRoleSelect = onRoleSelect;
        vm.isOperatorCreator = false;
        vm.isPasswordRequired = isPasswordRequired;
        vm.isAdmin = isAdmin;

        initialize();

        function initialize() {
            var login = Session.get();
            if (login && login.operatorId) {
                vm.isOperatorCreator = true;

                vm.operators = _.filter(vm.operators, function(o) { return o.id === login.operatorId; });
                vm.user.operatorId = login.operatorId;

                vm.roles = _.filter(vm.roles, function(r) { return !isAdmin(r.id); });
            }
        }

        function isAdmin(role) { return (role || vm.user.role) === 'ADMIN'; }
        function isApi() { return vm.user.role === 'OPERATOR_API';}

        function isPasswordRequired() {
            return isNewUser && !isApi();
        }

        function onOperatorSelect(item, model) {
            if (isAdmin()) {
                vm.user.role = undefined;
            }
        }

        function onRoleSelect(item, model) {
            if (isAdmin(model)) {
                vm.user.operatorId = undefined;
            }
        }

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
