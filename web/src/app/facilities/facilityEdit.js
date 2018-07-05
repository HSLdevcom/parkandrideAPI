// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.facilityEdit', [
        'ui.router',
        'parkandride.contacts',
        'parkandride.operators',
        'parkandride.SchemaResource',
        'parkandride.ContactResource',
        'parkandride.FacilityResource',
        'parkandride.facilityMap',
        'parkandride.layout',
        'parkandride.multilingual',
        'parkandride.facilityContacts',
        'parkandride.pricing',
        'parkandride.tags',
        'parkandride.address',
        'parkandride.pricingList',
        'parkandride.submitUtil',
        'parkandride.pricingManager',
        'showErrors'
    ]);

    m.config(function($stateProvider) {
        $stateProvider.state('facility-create', { // dot notation in ui-router indicates nested ui-view
            parent: 'hubstab',
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
                }
            }
        });
        $stateProvider.state('facility-edit', { // dot notation in ui-router indicates nested ui-view
            parent: 'hubstab',
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
                }
            }
        });
    });

    m.controller('FacilityEditCtrl', function($scope, $state, schema, FacilityResource, Session, Sequence, facility,
                                              aliasesPlaceholder, submitUtilFactory, pricingManager) {
        var self = this;
        var customPricing = [];
        self.context = "facilities";
        var submitUtil = submitUtilFactory($scope, self.context);

        self.capacityTypes = schema.capacityTypes.values;
        self.usages = schema.usages.values;
        self.dayTypes = schema.dayTypes.values;
        self.services = schema.services.values;
        self.paymentMethods = schema.paymentMethods.values;
        self.facilityStatuses = schema.facilityStatuses.values;
        self.pricingMethods = schema.pricingMethods.values;

        self.aliasesPlaceholder = aliasesPlaceholder;
        self.showUnavailableCapacityType = function(i) {
            var ucs = self.facility.unavailableCapacities;
            return i === 0 || ucs[i - 1].capacityType != ucs[i].capacityType;
        };

        self.facility = facility;
        if (!self.facility.operatorId) {
            var login = Session.get();
            self.facility.operatorId = login && login.operatorId;
        }

        self.editMode = (facility.id ? "ports" : "location");

        // Watch pricingMethod because:
        // 1) Invalid pricing rows should be validated iff pricingMethod is CUSTOM.
        // 2) Clearing pricing rows in save method would have to modify self.facility.pricing AND $scope.$apply() for
        //    these changes to be reflected in validation results.
        // 3) Having this customPricing for "discarded" rows allows us to revert to those at any time before
        //    validateAndSubmit success.
        $scope.$watch("editCtrl.facility.pricingMethod", function(newValue) {
            pricingManager.setPricingMethod(newValue);
        });

        self.save = function(form) {
            var facility = _.cloneDeep(self.facility);
            submitUtil.validateAndSubmit(
                form,
                function() { return FacilityResource.save(facility); },
                function(id) { return $state.go('facility-view', {"id": id }); }
            );
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
