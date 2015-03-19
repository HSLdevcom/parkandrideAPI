// Copyright © 2015 HSL

(function() {
    var m = angular.module('parkandride.submitUtil', [
        'parkandride.components.violations'
    ]);

    m.factory('submitUtilFactory', function(EVENTS, violationsManager) {
        return function(scope, context) {
            var api = {};
            api.submit = submit;
            api.validateAndSubmit = validateAndSubmit;
            return api;

            function handleSubmitReject(rejection) {
                if (rejection.status == 400 && rejection.data.violations) {
                    violationsManager.setViolations(context, rejection.data.violations);
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
                    violationsManager.setViolations(context, [{ path: "", type: "BasicRequirements" }]);
                }
            }
        };
    });
})();
