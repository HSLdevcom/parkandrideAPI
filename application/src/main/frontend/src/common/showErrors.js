(function() {
    var showErrorsModule;

    showErrorsModule = angular.module('showErrors', []);

    showErrorsModule.directive('showErrors', function($timeout, showErrorsConfig) {
        var getShowSuccess, getTrigger, linkFn;
        var errorClass = "validation-error";
        getTrigger = function(options) {
            var trigger;
            trigger = showErrorsConfig.trigger;
            if (options && (options.trigger != null)) {
                trigger = options.trigger;
            }
            return trigger;
        };
        getShowSuccess = function(options) {
            var showSuccess;
            showSuccess = showErrorsConfig.showSuccess;
            if (options && (options.showSuccess != null)) {
                showSuccess = options.showSuccess;
            }
            return showSuccess;
        };
        linkFn = function(scope, el, attrs, formCtrl) {
            var blurred, elName, options, showSuccess, toggleClasses, trigger;
            blurred = false;
            options = scope.$eval(attrs.showErrors);
            showSuccess = getShowSuccess(options);
            trigger = getTrigger(options);
            elName = el.attr('name');
            if (!elName) {
                throw "show-errors element has no 'name' attribute";
            }
            el.bind(trigger, function() {
                blurred = true;
                return toggleClasses(formCtrl[elName].$invalid);
            });
            scope.$watch(function() {
                return formCtrl[elName] && formCtrl[elName].$invalid;
            }, function(invalid) {
                if (!blurred) {
                    return;
                }
                return toggleClasses(invalid);
            });
            scope.$on('show-errors-check-validity', function() {
                console.log(elName);
                return toggleClasses(formCtrl[elName].$invalid);
            });
            scope.$on('show-errors-reset', function() {
                return $timeout(function() {
                    el.removeClass(errorClass);
                    el.removeClass('has-success');
                    return blurred = false;
                }, 0, false);
            });
            return toggleClasses = function(invalid) {
                el.toggleClass(errorClass, invalid);
                if (showSuccess) {
                    return el.toggleClass('has-success', !invalid);
                }
            };
        };
        return {
            restrict: 'A',
            require: '^form',
            compile: function(elem, attrs) {
                return linkFn;
            }
        };
    });

    showErrorsModule.provider('showErrorsConfig', function() {
        var _showSuccess, _trigger;
        _showSuccess = false;
        _trigger = 'blur';
        this.showSuccess = function(showSuccess) {
            return _showSuccess = showSuccess;
        };
        this.trigger = function(trigger) {
            return _trigger = trigger;
        };
        this.$get = function() {
            return {
                showSuccess: _showSuccess,
                trigger: _trigger
            };
        };
    });
})();