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
                    controller: 'CreateCtrl',
                    templateUrl: 'facilities/create.tpl.html'
                }
            },
            data: { pageTitle: 'Create Facility' },
            resolve: {
                capacityTypes: function(FacilityService) {
                    return FacilityService.getCapacityTypes();
                }
            }
        });
    });

    m.controller('CreateCtrl', function($scope, $state, FacilityService, Facility, capacityTypes) {
        $scope.facility = {
            aliases: [],
            capacities: _.map(capacityTypes, function(capacityType) {
                return {
                    capacityType: capacityType,
                    built: null,
                    unavailable: null
                };
            })
        };

        $scope.addFacility = function() {
            var facility = _.cloneDeep($scope.facility);
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

