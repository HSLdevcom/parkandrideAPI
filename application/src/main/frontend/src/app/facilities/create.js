(function() {
    var m = angular.module('facilities.create', [
        'ui.router',
        'parkandride.services.facilities',
        'parkandride.resources.facilities',
        'parkandride.directives.map',
        'ngTagsInput'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('facilities-create', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/create', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'CreateCtrl as createCtrl',
                    templateUrl: 'facilities/create.tpl.html'
                }
            },
            data: { pageTitle: 'Create Facility' },
            resolve: {
                capacityTypes: function(FacilityService) {
                    return FacilityService.getCapacityTypes();
                },
                facility: function() {
                    return {
                        aliases: [],
                        capacities: {}
                    };
                }
            }
        });
        $stateProvider.state('facilities-edit', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/edit/:id', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'CreateCtrl as createCtrl',
                    templateUrl: 'facilities/create.tpl.html'
                }
            },
            data: { pageTitle: 'Edit Facility' },
            resolve: {
                capacityTypes: function(FacilityService) {
                    return FacilityService.getCapacityTypes();
                },
                facility: function($stateParams, FacilityService) {
                    return FacilityService.getFacility($stateParams.id);
                }
            }
        });
    });

    m.controller('CreateCtrl', function($state, FacilityService, Facility, capacityTypes, facility) {

        facility.capacities = _.map(capacityTypes, function(capacityType) {
            var existing = _.find(facility.capacities, function(c) { return c.capacityType == capacityType; }) || {};
            return {
                capacityType: capacityType,
                built: existing.built,
                unavailable: existing.unavailable
            };
        });

        facility.aliases = _.map(facility.aliases, function(a) { return { text: a }; });

        this.facility = facility;

        this.addFacility = function() {
            var facility = _.cloneDeep(this.facility);
            facility.aliases = _.map(facility.aliases, function(alias) { return alias.text; });
            FacilityService.save(facility).then(function(id){
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
            templateUrl: 'facilities/create-navi.tpl.html'
        };
    });
})();

