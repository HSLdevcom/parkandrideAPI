(function() {
    var m = angular.module('facilities.create', [
        'ui.router'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facilities-create', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/create', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'CreateCtrl',
                    templateUrl: 'facilities/create.tpl.html'
                }
            },
            data: { pageTitle: 'Create Facility' }
        });
    });

    m.controller('CreateCtrl', CreateController);
    function CreateController($state, Restangular) {
        this.facility = {};
        this.addFacility = function() {
            var dummyBorder =   {
                "type": "Polygon",
                    "coordinates": [[
                    [60.25055, 25.010827],
                    [60.250023, 25.011867],
                    [60.250337, 25.012479],
                    [60.250886, 25.011454],
                    [60.25055, 25.010827]
                ]]
            };

            this.facility.border = dummyBorder;
            Restangular.all('facilities').post(this.facility);
            $state.go('facilities');
        };
    }

    m.directive('aliases', function() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attr, ngModel) {
                function fromView(text) {
                    return (text || '').split(/\s*,\s*/);
                }
                ngModel.$parsers.push(fromView);
            }
        };
    });
})();

