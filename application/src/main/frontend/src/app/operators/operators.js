(function() {
    var m = angular.module('parkandride.operators', [
        'ui.router',
        'parkandride.OperatorResource',
        'parkandride.layout',
        'parkandride.multilingual',
        'parkandride.util',
        'showErrors'
    ]);


    m.controller("OperatorEditCtrl", function ($scope, $modalInstance, OperatorResource, operator, create, EVENTS) {
        $scope.operator = operator;
        $scope.titleKey = 'operators.action.' + (create ? 'new' : 'edit');

        function saveOperator() {
            OperatorResource.save(operator).then(
                function(operator) {
                    $scope.operator = operator;
                    $modalInstance.close($scope.operator);
                },
                function(rejection) {
                    if (rejection.status == 400 && rejection.data.violations) {
                        $scope.violations = rejection.data.violations;
                    }
                }
            );
        }

        $scope.ok = function (form) {
            $scope.$broadcast(EVENTS.showErrorsCheckValidity);
            if (form.$valid) {
                saveOperator();
            } else {
                $scope.violations = [{
                    path: "",
                    type: "BasicRequirements"
                }];
            }
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });

    m.factory("editOperator", function($modal, OperatorResource) {
        return function(operator, create) {
            var modalInstance = $modal.open({
                templateUrl: 'operators/operatorEdit.tpl.html',
                controller: 'OperatorEditCtrl',
                resolve: {
                    operator: function () {
                        return _.cloneDeep(operator);
                    },
                    create: function() {
                        return create;
                    }
                }
            });
            return modalInstance.result;
        };
    });

    m.config(function($stateProvider) {
        $stateProvider.state('operator-list', {
            parent: 'root',
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
        $scope.common.translationPrefix = "operators";

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
                mandatory: '@'
            },
            templateUrl: 'operators/operatorSelect.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.allOperators = [];
                OperatorResource.listOperators().then(function(response) {
                    console.log(response);
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
