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
        'parkandride.tags',
        'parkandride.address',
        'showErrors'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('facility-create', { // dot notation in ui-router indicates nested ui-view
            parent: 'root',
            url: '/facilities/create', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilityEditCtrl as editCtrl',
                    templateUrl: 'facilities/facilityEdit.tpl.html'
                }
            },
            data: { pageTitle: 'Create Facility' },
            resolve: {
                // This is a hack, but I found no other way to ensure that tags-input placeholder translation works on page reload
                aliasesPlaceholder: function($translate) {
                    return $translate("facilities.aliases.placeholder");
                },
                facility: function(FacilityResource) {
                    return FacilityResource.newFacility();
                },
                services: function(ServiceResource) {
                    return ServiceResource.listServices().then(function(response) { return response.results; });
                },
                contacts: function(ContactResource) {
                    return ContactResource.listContacts().then(function(response) { return response.results; });
                }
            }
        });
        $stateProvider.state('facility-edit', { // dot notation in ui-router indicates nested ui-view
            parent: 'root',
            url: '/facilities/edit/:id',
            views: {
                "main": {
                    controller: 'FacilityEditCtrl as editCtrl',
                    templateUrl: 'facilities/facilityEdit.tpl.html'
                }
            },
            data: { pageTitle: 'Edit Facility' },
            resolve: {
                // This is a hack, but I found no other way to ensure that tags-input placeholder translation works on page reload
                aliasesPlaceholder: function($translate) {
                    return $translate("facilities.aliases.placeholder");
                },
                facility: function($stateParams, FacilityResource) {
                    return FacilityResource.getFacility($stateParams.id);
                },
                services: function(ServiceResource) {
                    return ServiceResource.listServices().then(function(response) { return response.results; });
                },
                contacts: function(ContactResource) {
                    return ContactResource.listContacts().then(function(response) { return response.results; });
                }
            }
        });
    });

    m.controller('FacilityEditCtrl', function($scope, $state, schema, FacilityResource, facility, aliasesPlaceholder, services, contacts, editContact) {
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
        self.createContact = function(type) {
            editContact({}, true).then(function(contact) {
                self.contacts[contact.id] = contact;
                self.facility.contacts[type] = contact.id;
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
