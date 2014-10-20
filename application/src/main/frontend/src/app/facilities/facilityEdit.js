(function() {
    var m = angular.module('parkandride.facilityEdit', [
        'ui.router',
        'parkandride.FacilityResource',
        'parkandride.facilityMap',
        'ngTagsInput'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('facility-create', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/create', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilityEditCtrl as editCtrl',
                    templateUrl: 'facilities/facilityEdit.tpl.html'
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
        $stateProvider.state('facility-edit', { // dot notation in ui-router indicates nested ui-view
            url: '/facilities/edit/:id',
            views: {
                "main": {
                    controller: 'FacilityEditCtrl as editCtrl',
                    templateUrl: 'facilities/facilityEdit.tpl.html'
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

    m.controller('FacilityEditCtrl', function($state, FacilityResource, capacityTypes, facility) {
        this.capacityTypes = capacityTypes;

        facility.aliases = _.map(facility.aliases, function(a) { return { text: a }; });

        this.facility = facility;

        this.saveFacility = function() {
            var facility = _.cloneDeep(this.facility);
            facility.aliases = _.map(facility.aliases, function(alias) { return alias.text; });
            FacilityResource.save(facility).then(function(id){
                $state.go('facility-view', { "id": id });
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

    m.directive('facilityEditNaviAbove', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityEditNaviAbove.tpl.html'
        };
    });

    m.directive('facilityEditNaviBelow', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityEditNaviBelow.tpl.html'
        };
    });

})();
