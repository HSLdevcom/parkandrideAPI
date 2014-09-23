(function() {
    var m = angular.module('facilities.create', [
        'ui.router',
        'ngBoilerplate.services.facilities',
        'ngBoilerplate.resources.facilities'
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
    function CreateController($state, FacilityService, Facility) {
        this.facility = Facility.build({});

        this.addFacility = function() {
            FacilityService.save(this.facility).then(function(id){
                $state.go('facilities-view', { "id": id });
            });
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

    m.controller('CapacityCtrl', CapacityController);
    function CapacityController(Capacity) {
        this.capacity = new Capacity();
        this.addCapacity = function(facility){
            facility.capacities.push(this.capacity);
            this.capacity = new Capacity();
        };
    }
})();

