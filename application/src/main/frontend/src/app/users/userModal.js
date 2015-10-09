// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.users.userModal', [
        'ui.bootstrap',
        'ui.router',
        'ui.select',
        'parkandride.auth',
        'parkandride.modalUtil',
        'parkandride.UserResource',
        'parkandride.OperatorResource',
        'parkandride.i18n',
        'parkandride.components.violations',
        'showErrors'
    ]);

    m.factory('userModal', function($uibModal, OperatorResource) {
        return {
            open: function(user) {
                return $uibModal.open({
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

    m.controller('UserModalCtrl', function($scope, $modalInstance, $translate, schema, user, EVENTS, UserResource, operators, Session, modalUtilFactory) {
        var vm = this;
        vm.context = "users";
        var isNewUser = !user;
        var modalUtil = modalUtilFactory($scope, vm.context, $modalInstance);

        vm.titleKey = 'users.action.' + (isNewUser ? 'new' : 'edit');
        vm.user = user || {};
        vm.save = save;
        vm.cancel = cancel;
        vm.roles = schema.roles.values;
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
            modalUtil.validateAndSubmit(form, function() { return UserResource.save(vm.user); });
        }

        function cancel() {
            $modalInstance.dismiss();
        }
    });
})();
