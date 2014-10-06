(function() {
    var m = angular.module('parkandride.facilityCreate', [
        'ui.router',
        'parkandride.FacilityResource',
        'parkandride.directives.map',
        'ngTagsInput'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('facilities-create', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/create', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilityCreateCtrl as createCtrl',
                    templateUrl: 'facilities/facilityCreate.tpl.html'
                }
            },
            data: { pageTitle: 'Create Facility' },
            resolve: {
                capacityTypes: function(FacilityResource) {
                    return FacilityResource.getCapacityTypes();
                },
                facility: function(FacilityResource) {
                    return FacilityResource.newFacility();
                }
            }
        });
        $stateProvider.state('facilities-edit', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/edit/:id', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilityCreateCtrl as createCtrl',
                    templateUrl: 'facilities/facilityCreate.tpl.html'
                }
            },
            data: { pageTitle: 'Edit Facility' },
            resolve: {
                capacityTypes: function(FacilityResource) {
                    return FacilityResource.getCapacityTypes();
                },
                facility: function($stateParams, FacilityResource) {
                    return FacilityResource.getFacility($stateParams.id);
                }
            }
        });
    });

    m.controller('FacilityCreateCtrl', function($state, FacilityResource, capacityTypes, facility) {

        facility.capacities = _.map(capacityTypes, function(capacityType) {
            return FacilityResource.getOrCreateCapacity(facility, capacityType);
        });

        facility.aliases = _.map(facility.aliases, function(a) { return { text: a }; });

        this.facility = facility;

        this.addFacility = function() {
            var facility = _.cloneDeep(this.facility);
            facility.aliases = _.map(facility.aliases, function(alias) { return alias.text; });
            FacilityResource.save(facility).then(function(id){
                $state.go('facilities-view', { "id": id });
            });
        };
    });

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

    m.directive('createNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityCreateNavi.tpl.html'
        };
    });
})();

