(function() {
    var m = angular.module('parkandride.components.violations', [
        'parkandride.i18n'
    ]);

    m.directive('violations', violations);
    m.controller('ViolationsCtrl', ViolationsCtrl);
    m.factory('violationsManager', violationsManager);

    function violations() {
        return {
            restrict: 'E',
            scope: {
                prefix: '@'
            },
            bindToController: true,
            controller: 'ViolationsCtrl',
            controllerAs: 'ctrl',
            templateUrl: 'components/violations/violations.tpl.html'
        };
    }

    function ViolationsCtrl(violationsManager) {
        var vm = this;
        vm.model = violationsManager.model;
        vm.hasViolations = violationsManager.hasViolations;
        vm.getLabel = getLabel;

        function getLabel(violation) {
            return vm.prefix + (violation.path ? '.' + violation.path : '') + '.label';
        }
    }

    function violationsManager() {
        var model = {
            violations: []
        };

        var api = {};
        api.setViolations = setViolations;
        api.hasViolations = hasViolations;
        api.model = model;

        function setViolations(violations) {
            // TODO filter out duplicates
            angular.copy(violations, model.violations);
        }

        function hasViolations() {
            return !_.isEmpty(model.violations);
        }

        return api;
    }

})();
