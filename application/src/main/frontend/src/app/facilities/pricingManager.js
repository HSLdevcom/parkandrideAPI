(function() {
    var m = angular.module('parkandride.pricingManager', []);

    m.factory('pricingManager', function(Sequence) {
        var self = {};
        var clipboard = initClipboard();
        var selections = initSelections();

        self.model = {
            allSelected: false,
            selections: selections.model
        };

        self.onSelectAllChange = onSelectAllChange;
        self.onSelectRowChange = onSelectRowChange;
        self.addRow = addRow;
        self.addToClipboard = clipboard.add;
        self.isClipboardEmpty = clipboard.isEmpty;
        self.isInClipboard = clipboard.contains;
        self.copyPricingRows = copyPricingRows;
        self.deletePricingRows = deletePricingRows;
        self.pastePricingRows = pastePricingRows;
        self.pastePricingValues = pastePricingValues;
        self.init = init;
        return self;

        function init(facility) {
            self.pricing = facility.pricing;
            _.forEach(self.pricing, function(p) { p._id = Sequence.nextval();});
        }

        function copyPricingRows(firstOnly) {
            clipboard.clear();
            _.forEach(self.pricing, function(p) {
                if (selections.isSelected(p)) {
                    selections.applyChange(p, false);

                    clipboard.add(p);
                    return !firstOnly;
                }
            });
        }

        function deletePricingRows() {
            clipboard.clear();
            _.forEachRight(self.pricing, function(p, i) {
                if (selections.isSelected(p)) {
                    self.pricing.splice(i, 1);
                    selections.applyChange(p, false);
                }
            });
            self.model.allSelected = false;
        }

        function pastePricingRows() {
            _.forEach(clipboard.rows, function(p) {
                var newPricing = _.cloneDeep(p);
                delete newPricing.$$hashKey; // TODO consider using angular.clone
                newPricing._id = Sequence.nextval();
                self.pricing.push(newPricing);

                if (clipboard.rows.length > 1) {
                    selections.applyChange(newPricing, true);
                }
            });
            self.model.allSelected = false;
        }

        function pastePricingValues(property) {
            var len = clipboard.rows.length;
            if (len === 0) {
                return;
            }

            var j = 0;
            _.forEach(self.pricing, function(p) {
                if (selections.isSelected(p)) {
                    var value = clipboard.rows[j++ % len][property];
                    p[property] = _.cloneDeep(value);
                }
            });
        }

        function addRow() {
            selections.clear();
            clipboard.clear();

            var p = {};
            p._id = Sequence.nextval();
            self.pricing.push(p);

            self.model.allSelected = false;
        }

        function onSelectAllChange() {
            if (self.model.allSelected === isAllRowsSelected()) {
                return;
            }

            _.forEach(self.pricing, function(p) {
                selections.applyChange(p, self.model.allSelected);
            });
        }

        function onSelectRowChange(pricing) {
            selections.updateCount(pricing);
            self.model.allSelected = isAllRowsSelected();
        }

        function isAllRowsSelected() {
            return selections.isAllSelected(self.pricing.length);
        }

        function initClipboard() {
            var self = {};
            self.rows = [];
            self.ids = {};

            self.add = function(p) {
                self.rows.push(p);
                self.ids[p._id] = true;
            };

            self.clear = function() {
                self.rows = [];
                self.ids = {};
            };

            self.isEmpty = function() {
                return self.rows.length === 0;
            };

            self.contains = function(p) {
                return self.ids[p._id] === true;
            };

            return self;
        }

        function initSelections() {
            var self = {};
            var model = { count: 0 };
            self.model = model;

            self.updateCount = function(p) {
                model.count += model[p._id] ? +1 : -1;
            };

            self.clear = function() {
                for (var s in model) {
                    delete model[s];
                }
                model.count = 0;
            };

            self.isAllSelected = function(allCount) {
                return model.count === allCount;
            };

            self.applyChange = function(p, isSelected) {
                if (model[p._id] !== isSelected) {
                    model[p._id] = isSelected;
                    self.updateCount(p);
                }
            };

            self.isSelected = function(p) {
                return model[p._id] === true;
            };

            return self;
        }
    });
})();
