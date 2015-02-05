(function() {
    var m = angular.module('parkandride.modalUtil', [
        'parkandride.components.violations'
    ]);

    m.factory('modalUtilFactory', function(EVENTS, violationsManager) {
        return function(scope, modalInstance) {
            var api = {};
            api.handleSubmitSuccess = handleSubmitSuccess;
            api.handleSubmitReject = handleSubmitReject;
            api.submit = submit;
            api.validateAndSubmit = validateAndSubmit;
            return api;

            function handleSubmitSuccess(success) {
                modalInstance.close(success);
            }

            function handleSubmitReject(rejection) {
                if (rejection.status == 400 && rejection.data.violations) {
                    violationsManager.setViolations(rejection.data.violations);
                }
            }

            function submit(submitFn) {
                submitFn().then(handleSubmitSuccess, handleSubmitReject);
            }

            function validateAndSubmit(form, submitFn) {
                scope.$broadcast(EVENTS.showErrorsCheckValidity);
                if (form.$valid) {
                    submit(submitFn);
                } else {
                    violationsManager.setViolations([{ path: "", type: "BasicRequirements" }]);
                }
            }
        };
    });
})();
