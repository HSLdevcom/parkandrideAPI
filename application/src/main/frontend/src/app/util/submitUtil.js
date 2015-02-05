(function() {
    var m = angular.module('parkandride.submitUtil', [
        'parkandride.components.violations'
    ]);

    m.factory('submitUtilFactory', function(EVENTS, violationsManager) {
        return function(scope) {
            var api = {};
            api.submit = submit;
            api.validateAndSubmit = validateAndSubmit;
            return api;

            function handleSubmitReject(rejection) {
                if (rejection.status == 400 && rejection.data.violations) {
                    violationsManager.setViolations(rejection.data.violations);
                }
            }

            function submit(submitFn, submitSuccessFn) {
                submitFn().then(submitSuccessFn, handleSubmitReject);
            }

            function validateAndSubmit(form, submitFn, submitSuccessFn) {
                scope.$broadcast(EVENTS.showErrorsCheckValidity);
                if (form.$valid) {
                    submit(submitFn, submitSuccessFn);
                } else {
                    violationsManager.setViolations([{ path: "", type: "BasicRequirements" }]);
                }
            }
        };
    });
})();
