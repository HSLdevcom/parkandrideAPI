(function() {
    var m = angular.module('parkandride.pricingManager', []);

    m.factory('pricingManager', function(Sequence) {
        var self = {};

        self.selections = {
            // Selected pricing IDs as boolean-values properties
            count: 0 // Selected row count for efficient "if all selected" check
        };
        self.data = {
            allSelected: false
        };
        self.onSelectAllChange = onSelectAllChange;
        self.onSelectRowChange = onSelectRowChange;
        self.addRow = addRow;
        self.init = init;

        function init(facility) {
            self.pricing = facility.pricing;
            _.forEach(self.pricing, function(p) { p._id = Sequence.nextval();});
        }

        function addRow() {
            var p = {};
            p._id = Sequence.nextval();
            self.pricing.push(p);

            self.data.allSelected = false;
        }

        function onSelectAllChange() {
            if (self.data.allSelected === isAllRowsSelected()) {
                return;
            }

            for (var i = self.pricing.length - 1; i >= 0; i--) {
                applySelectChange(self.pricing[i]._id, self.data.allSelected);
            }
        }

        function onSelectRowChange(pricing) {
            self.selections.count += (self.selections[pricing._id] ? +1 : -1);
            self.data.allSelected = isAllRowsSelected();
        }

        function isAllRowsSelected() {
            return self.selections.count === self.pricing.length;
        }

        function applySelectChange(pricingId, isSelected) {
            if (self.selections[pricingId] !== isSelected) {
                self.selections.count += (isSelected ? +1 : -1);
            }
            self.selections[pricingId] = isSelected;
        }

        return self;
    });
})();