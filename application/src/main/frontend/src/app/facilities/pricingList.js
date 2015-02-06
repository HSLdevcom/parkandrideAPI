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
        vm.advancedMode = false;
        vm.model = pricingManager.model;
        vm.onSelectAllChange = pricingManager.onSelectAllChange;
        vm.addPricingRow = pricingManager.addRow;
        vm.isClipboardEmpty = pricingManager.isClipboardEmpty;
        vm.copyPricingRows = pricingManager.copyPricingRows;
        vm.deletePricingRows = pricingManager.deletePricingRows;
        vm.pastePricingRows = pricingManager.pastePricingRows;
        vm.pastePricingValues = pricingManager.pastePricingValues;

        vm.hasPricingRows = hasPricingRows;
        vm.getPricingRowClasses = getPricingRowClasses;

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

            var classes = (vm.model.selections[p._id] ? 'selected' : 'unselected');
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
