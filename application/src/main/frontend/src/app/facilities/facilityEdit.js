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

    m.controller('FacilityEditCtrl', function($scope, $state, schema, FacilityResource, Session, Sequence, facility, aliasesPlaceholder) {
        var self = this;
        $scope.common.translationPrefix = "facilities";
        self.advancedMode = false;
        self.capacityTypes = schema.capacityTypes.values;
        self.usages = schema.usages.values;
        self.dayTypes = schema.dayTypes.values;
        self.services = schema.services.values;
        self.paymentMethods = schema.paymentMethods.values;
        self.facilityStatuses = schema.facilityStatuses.values;

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

        $scope.allSelected = false;
        $scope.selections = {
            // Selected pricing IDs as boolean-values properties
            count: 0 // Selected row count for efficient "if all selected" check
        };
        $scope.$watch("allSelected", function(checked) {
            if (checked === isAllRowsSelected()) {
                return;
            }
            var pricingRows = self.facility.pricing;
            for (var i = pricingRows.length - 1; i >= 0; i--) {
                var id = pricingRows[i]._id;
                setSelected(id, checked);
            }
        });
        $scope.$watch("selections.count", function(newCount) {
            $scope.allSelected = newCount === self.facility.pricing.length;
        });

        _.forEach(self.facility.pricing, function(pricing) {
            pricing._id = Sequence.nextval();
        });

        self.addPricingRow = function() {
            self.clearSelections();
            self.clearClipboard();
            var newPricing = {};
            newPricing._id = Sequence.nextval();
            self.facility.pricing.push(newPricing);
            $scope.allSelected = false;
        };

        $scope.pricingClipboard = [];
        $scope.pricingClipboardIds = {};

        self.copyPricingRows = function(firstOnly) {
            var pricingRows = self.facility.pricing;
            self.clearClipboard();
            for (var i=0; i < pricingRows.length; i++) {
                var id = pricingRows[i]._id;
                if (isSelected(id)) {
                    setSelected(id, false);
                    $scope.pricingClipboard.push(pricingRows[i]);
                    $scope.pricingClipboardIds[id] = true;
                    if (firstOnly) {
                        return;
                    }
                }
            }
        };
        self.deletePricingRows = function() {
            self.clearClipboard();
            var pricingRows = self.facility.pricing;
            for (var i=pricingRows.length - 1; i >= 0; i--) {
                var id = pricingRows[i]._id;
                if (isSelected(id)) {
                    pricingRows.splice(i, 1);
                    setSelected(id, false);
                }
            }
            $scope.allSelected = false;
        };
        self.pastePricingRows = function() {
            for (var i=0; i < $scope.pricingClipboard.length; i++) {
                var id = $scope.pricingClipboard[i]._id;
                var newPricing = _.cloneDeep($scope.pricingClipboard[i]);
                delete newPricing.$$hashKey;

                newPricing._id = Sequence.nextval();
                self.facility.pricing.push(newPricing);
                if ($scope.pricingClipboard.length > 1) {
                    setSelected(newPricing._id, true);
                }
            }
            $scope.allSelected = false;
        };
        self.pastePricingValues = function(property) {
            var len = $scope.pricingClipboard.length;
            if (len === 0) {
                return;
            }
            var pricingRows = self.facility.pricing;
            var j=0;
            for (var i=0; i < pricingRows.length; i++) {
                var id = pricingRows[i]._id;
                if (isSelected(id)) {
                    var value = $scope.pricingClipboard[j++ % len][property];
                    pricingRows[i][property] = _.cloneDeep(value);
                }
            }
        };
        self.clearClipboard = function() {
            $scope.pricingClipboard = [];
            $scope.pricingClipboardIds = {};
        };
        self.clearSelections = function() {
            for (var s in $scope.selections) {
                delete $scope.selections[s];
            }
            $scope.selections.count = 0;
            $scope.allSelected = false;
        };

        self.hasPricingRows = function() {
            return self.facility.pricing.length > 0;
        };
        self.isNewPricingGroup = function(i) {
            if (i === 0) {
                return false;
            }
            var pricingRows = self.facility.pricing;
            return pricingRows[i-1].capacityType !== pricingRows[i].capacityType ||
                pricingRows[i-1].usage !== pricingRows[i].usage;
        };
        self.getPricingRowClasses = function(pricing, i) {
            var classes = ($scope.selections[pricing._id] ? 'selected' : 'unselected');
            if (self.advancedMode && $scope.pricingClipboardIds[pricing._id]) {
                classes += ' on-clipboard';
            }
            if (self.isNewPricingGroup(i)) {
                classes += ' new-pricing-group';
            }
            return classes;
        };

        self.saveFacility = function() {
            var facility = _.cloneDeep(self.facility);
            FacilityResource.save(facility).then(function(id){
                $state.go('facility-view', { "id": id });
            });
        };

        function isAllRowsSelected() {
            return $scope.selections.count === self.facility.pricing.length;
        }
        function isSelected(pricingId) {
            return $scope.selections[pricingId];
        }
        function setSelected(pricingId, selected) {
            if ($scope.selections[pricingId] !== selected) {
                $scope.selections.count += (selected ? +1 : -1);
            }
            $scope.selections[pricingId] = selected;
        }

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
