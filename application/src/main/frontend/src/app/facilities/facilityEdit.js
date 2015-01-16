(function() {
    var m = angular.module('parkandride.facilityEdit', [
        'ui.router',
        'parkandride.contacts',
        'parkandride.operators',
        'parkandride.ServiceResource',
        'parkandride.PaymentMethodResource',
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
                },
                services: function(ServiceResource) {
                    return ServiceResource.listServices().then(function(response) { return response.results; });
                },
                paymentMethods: function(PaymentMethodResource) {
                    return PaymentMethodResource.listPaymentMethods().then(function(response) { return response.results; });
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
                },
                services: function(ServiceResource) {
                    return ServiceResource.listServices().then(function(response) { return response.results; });
                },
                paymentMethods: function(PaymentMethodResource) {
                    return PaymentMethodResource.listPaymentMethods().then(function(response) { return response.results; });
                }
            }
        });
    });

    m.controller('FacilityEditCtrl', function($scope, $state, schema, FacilityResource, Session, Sequence, facility, aliasesPlaceholder, services, paymentMethods) {
        var self = this;
        $scope.common.translationPrefix = "facilities";
        self.capacityTypes = schema.capacityTypes;
        self.usages = schema.usages;
        self.dayTypes = schema.dayTypes;
        self.services = services;
        self.paymentMethods = paymentMethods;
        self.aliasesPlaceholder = aliasesPlaceholder;

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
        self.cutPricingRows = function() {
            $scope.pricingClipboard = [];
            $scope.pricingClipboardIds = {};
            var pricingRows = self.facility.pricing;
            for (var i=pricingRows.length - 1; i >= 0; i--) {
                var id = pricingRows[i]._id;
                if (isSelected(id)) {
                    $scope.pricingClipboard.splice(0, 0, pricingRows[i]);
                    // No need to register pricingClipboardIds: if rows are pasted back, they get new id's
                    // $scope.pricingClipboardIds[id] = true;
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
                setSelected(newPricing._id, true);
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
            if ($scope.pricingClipboardIds[pricing._id]) {
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
