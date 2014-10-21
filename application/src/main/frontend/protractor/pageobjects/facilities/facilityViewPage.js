'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')();

    spec.view = $('.wdFacilityView');
    spec.name = $('.wdName');
    spec.aliases = $('.wdAliases');
    spec.toListButton = element.all(by.linkUiSref('facility-list')).first();
    spec.capacityTypes = element.all(by.css(".wdCapacityType"));

    that.getName = function () {
        return spec.name.getText();
    };

    that.getAliases = function () {
        return spec.aliases.getText().then(function(joined){
            return joined.split(", ");
        });
    };

    that.assertCapacities = function (capacities) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                expect($('.wd' + capacityType + prop).getText()).toEqual("" + capacity[prop]);
            }
        }
    };

    that.getCapacityTypes = function() {
        return capacitiesTable.getTypes();
    };

    that.toListView = function () {
        return spec.toListButton.click();
    };

    return that;
};
