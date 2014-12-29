(function() {
    var m = angular.module('parkandride.facilityEdit', [
        'ui.router',
        'parkandride.contacts',
        'parkandride.ServiceResource',
        'parkandride.ContactResource',
        'parkandride.FacilityResource',
        'parkandride.facilityMap',
        'parkandride.layout',
        'parkandride.multilingual',
        'parkandride.facilityContacts',
        'parkandride.tags',
        'parkandride.address',
        'showErrors'
    ]);

    var resolveBase = {
        aliasesPlaceholder: function($translate) { return $translate("facilities.aliases.placeholder"); },
        services: function(ServiceResource) { return ServiceResource.listServices().then(function(response) { return response.results; }); },
        contacts: function(ContactResource) { return ContactResource.listContacts().then(function(response) { return response.results; }); }
    };

    var resolveOnCreate = _.assign({
        facility: function(FacilityResource) { return FacilityResource.newFacility(); }
    }, resolveBase);

    var resolveOnEdit = _.assign({
        facility: function($stateParams, FacilityResource) { return FacilityResource.getFacility($stateParams.id); }
    }, resolveBase);

    var stateConfigBase = {
        parent: 'root',
        views: {
            "main": {
                controller: 'FacilityEditCtrl as editCtrl',
                templateUrl: 'facilities/facilityEdit.tpl.html'
            }
        }
    };

    var createStateConfig = _.assign({
        url: '/facilities/create',
        data: { pageTitle: 'Create Facility' },
        resolve: resolveOnCreate
    }, stateConfigBase);

    var editStateConfig = _.assign({
        url: '/facilities/edit/:id',
        data: { pageTitle: 'Edit Facility' },
        resolve: resolveOnEdit
    }, stateConfigBase);

    m.config(function($stateProvider) {
        $stateProvider.state('facility-create', createStateConfig);
        $stateProvider.state('facility-edit', editStateConfig);
    });

    m.controller('FacilityEditCtrl', function($scope, $state, schema, FacilityResource, facility, aliasesPlaceholder, services, contacts) {
        var self = this;
        $scope.common.translationPrefix = "facilities";
        self.capacityTypes = schema.capacityTypes;
        self.services = services;
        self.contacts = contacts;
        self.aliasesPlaceholder = aliasesPlaceholder;

        self.facility = facility;
        self.editMode = (facility.id ? "ports" : "location");

        self.saveFacility = function() {
            var facility = _.cloneDeep(self.facility);
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

    m.directive('facilityEditNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityEditNavi.tpl.html'
        };
    });

})();
