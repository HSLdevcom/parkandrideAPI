(function() {
    var m = angular.module('parkandride.components.violations', [
        'parkandride.i18n'
    ]);

    m.directive('violations', violations);
    m.controller('ViolationsCtrl', ViolationsCtrl);
    m.factory('violationsManager', violationsManager);

    function violations(violationsManager) {
        return {
            restrict: 'E',
            scope: {
                prefix: '@'
            },
            bindToController: true,
            controller: 'ViolationsCtrl',
            controllerAs: 'ctrl',
            templateUrl: 'components/violations/violations.tpl.html',
            link: function() {
                violationsManager.clear();
            }
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
        api.clear = clear;
        api.model = model;

        function setViolations(violations) {
            function filterDuplicates(violations) {
                var filtered = [];

                var duplicates = {};
                for (var i=0; i < violations.length; i++) {
                    var violation = violations[i];
                    violation.path = violation.path.replace(/\[\d+\]/, ""); // same violations in different indexes are considered the same
                    var violationKey = violation.path + "/" + violation.type;
                    if (!duplicates[violationKey]) {
                        duplicates[violationKey] = true;
                        filtered.push(violation);
                    }
                }

                return filtered;
            }

            console.log("set violations");
            angular.copy(filterDuplicates(violations), model.violations);
        }

        function hasViolations() {
            return !_.isEmpty(model.violations);
        }

        function clear() {
            console.log("clearing violations...");
            model.violations = [];
        }

        return api;
    }

})();
