(function() {
    var m = angular.module('parkandride.modalUtil', [
        'parkandride.submitUtil'
    ]);

    m.factory('modalUtilFactory', function(submitUtilFactory) {
        return function(scope, modalInstance) {

            var submitUtil = submitUtilFactory(scope);

            var api = {};
            api.submit = submit;
            api.validateAndSubmit = validateAndSubmit;
            return api;

            function handleSubmitSuccess(success) {
                modalInstance.close(success);
            }

            function submit(submitFn) {
                submitUtil.submit(submitFn, handleSubmitSuccess);
            }

            function validateAndSubmit(form, submitFn) {
                submitUtil.validateAndSubmit(form, submitFn, handleSubmitSuccess);
            }
        };
    });
})();
