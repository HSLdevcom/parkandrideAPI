(function() {
    var m = angular.module('parkandride.pricingList', [
        'parkandride.pricing',
        'parkandride.multilingual',
        'parkandride.pricingManager',
        'parkandride.i18n'
    ]);

    m.directive('pricingList', function() {
        return {
            restrict: 'E',
            scope: {
                pricings: '='
            },
            bindToController: true,
            controller: 'PricingListCtrl',
            controllerAs: 'ctrl',
            templateUrl: 'facilities/pricingList.tpl.html'
        };
    });

    m.controller('PricingListCtrl', function(pricingManager) {
        var vm = this;

        pricingManager.init(vm.pricings);
        vm.onSelectAllChange = pricingManager.onSelectAllChange;
        vm.addPricingRow = pricingManager.addRow;
        vm.isClipboardEmpty = pricingManager.isClipboardEmpty;
        vm.copyPricingRows = pricingManager.copyPricingRows;
        vm.deletePricingRows = pricingManager.deletePricingRows;
        vm.pastePricingRows = pricingManager.pastePricingRows;
        vm.pastePricingValues = pricingManager.pastePricingValues;

        vm.allSelected = allSelected;
        vm.advancedMode = false;
        vm.hasPricingRows = hasPricingRows;
        vm.getPricingRowClasses = getPricingRowClasses;

        vm.showColumnActions = showColumnActions;
        vm.isPasteDisabled = isPasteDisabled;
        vm.isCopyDisabled = isCopyDisabled;
        vm.isCopyFirstDisabled = isCopyFirstDisabled;
        vm.isRemoveDisabled = isRemoveDisabled;

        function allSelected(isSelected) {
            if (angular.isDefined(isSelected)) {
                pricingManager.model.allSelected = isSelected;
            }
            return pricingManager.model.allSelected;
        }

        function showColumnActions() {
            return vm.advancedMode && !vm.isClipboardEmpty() && pricingManager.model.selections.count > 0;
        }

        function isPasteDisabled() {
            return vm.isClipboardEmpty() || pricingManager.model.selections.count > 0;
        }

        function isCopyDisabled() {
            return pricingManager.model.selections.count === 0;
        }

        function isCopyFirstDisabled() {
            return pricingManager.model.selections.count < 2;
        }

        function isRemoveDisabled() {
            return pricingManager.model.selections.count === 0;
        }

        function hasPricingRows() {
            return vm.pricings.length > 0;
        }

        function getPricingRowClasses(p, i) {
            function isNewPricingGroup(i) {
                if (i === 0) { return false; }
                var previous = vm.pricings[i-1];
                var current = vm.pricings[i];
                return previous.capacityType !== current.capacityType || previous.usage !== current.usage;
            }

            var classes = (pricingManager.model.selections[p._id] ? 'selected' : 'unselected');
            if (vm.advancedMode && pricingManager.isInClipboard(p)) {
                classes += ' on-clipboard';
            }
            if (isNewPricingGroup(i)) {
                classes += ' new-pricing-group';
            }
            return classes;
        }
    });
})();
