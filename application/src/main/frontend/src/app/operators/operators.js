(function() {
    var m = angular.module('parkandride.operators', [
        'ui.router',
        'parkandride.OperatorResource',
        'parkandride.layout',
        'parkandride.multilingual',
        'parkandride.util',
        'parkandride.modalUtil',
        'showErrors'
    ]);

    m.controller("OperatorEditCtrl", function ($scope, $modalInstance, OperatorResource, operator, create, EVENTS, modalUtilFactory) {
        var vm = this;
        vm.context = "operators";

        var modalUtil = modalUtilFactory($scope, vm.context, $modalInstance);

        $scope.operator = operator;
        $scope.titleKey = 'operators.action.' + (create ? 'new' : 'edit');

        $scope.ok = function (form) {
            modalUtil.validateAndSubmit(form, function() { return OperatorResource.save(operator); });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss();
        };
    });

    m.factory("editOperator", function($modal, OperatorResource) {
        return function(operator, create) {
            var modalInstance = $modal.open({
                templateUrl: 'operators/operatorEdit.tpl.html',
                controller: 'OperatorEditCtrl as operatorEditCtrl',
                resolve: {
                    operator: function () {
                        return _.cloneDeep(operator);
                    },
                    create: function() {
                        return create;
                    }
                },
                backdrop: 'static'
            });
            return modalInstance.result;
        };
    });

    m.config(function($stateProvider) {
        $stateProvider.state('operator-list', {
            parent: 'operatorstab',
            url: '/operators',
            views: {
                "main": {
                    controller: 'OperatorListCtrl as listCtrl',
                    templateUrl: 'operators/operatorList.tpl.html'
                }
            },
            data: { pageTitle: 'Operators' },
            resolve: {
                operators: function(OperatorResource) {
                    return OperatorResource.listOperators();
                }
            }
        });
    });

    m.controller('OperatorListCtrl', function($scope, OperatorResource, editOperator, operators) {
        var self = this;
        self.operators = operators.results;

        self.create = function() {
            editOperator({}, true).then(function() {
                return OperatorResource.listOperators();
            }).then(function(operators) {
                self.operators = operators.results;
            });
        };
        self.edit = function(operator) {
            editOperator(operator, false).then(function() {
                return OperatorResource.listOperators();
            }).then(function(operators) {
                self.operators = operators.results;
            });
        };
    });

    m.directive('operatorListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'operators/operatorListNavi.tpl.html'
        };
    });

    m.directive('operatorSelect', function (OperatorResource, editOperator) {
        return {
            restrict: 'E',
            scope: {
                object: '=',
                mandatory: '='
            },
            templateUrl: 'operators/operatorSelect.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.allOperators = [];
                OperatorResource.listOperators().then(function(response) {
                    scope.allOperators = response.results;
                });
                scope.createOperator = function() {
                    editOperator({}, true).then(function(operator) {
                        scope.allOperators.push(operator);
                        scope.object.operatorId = operator.id;
                    });
                };
            }
        };
    });

})();
