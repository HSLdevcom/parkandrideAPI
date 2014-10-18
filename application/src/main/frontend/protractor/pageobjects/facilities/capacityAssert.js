"use strict";

module.exports = function() {
    var self = {};

    self.assertInOrderIfDisplayed = function(actualTypes, expectedTypeOrder) {
        for (var i = 0; i < expectedTypeOrder.length; i++) {
            if (expectedTypeOrder[i]) {
                expect(actualTypes.get(i).isDisplayed()).toBe(true);
                expect(actualTypes.get(i).getText()).toBe(expectedTypeOrder[i]);
            } else {
                expect(actualTypes.get(i).isDisplayed()).toBe(false);
            }
        }
    };

    self.assertInOrder = function(actualTypes, expectedTypeOrder) {
        expect(actualTypes.count()).toBe(expectedTypeOrder.length);
        for (var i = 0; i < expectedTypeOrder.length; i++) {
            expect(actualTypes.get(i).isDisplayed()).toBe(true);
            expect(actualTypes.get(i).getText()).toBe(expectedTypeOrder[i]);
        }
    };

    return self;
};