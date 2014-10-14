var HubViewPage = (function() {
    var _ = require('lodash');
    var FacilityFixture = require('../../fixtures/fixtures.js').FacilityFixture;

    function HubViewPage() {
        this.view = $('.wdHubView');
        this.name = $('.wdName');
    }

    HubViewPage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    HubViewPage.prototype.getName = function() {
        return this.name.getText();
    };

    HubViewPage.prototype.assertCapacities = function(facilities) {
        var sum = _.reduce(facilities, function(acc, facility) {
            return acc.incCapacity(facility);
        });

        for (var capacityType in sum.capacities) {
            var capacity = sum.capacities[capacityType];
            for (var prop in capacity) {
                expect($('.wd' + capacityType + prop).getText()).toEqual("" + capacity[prop]);
            }
        }
    };

    return HubViewPage;
})();

module.exports = HubViewPage;