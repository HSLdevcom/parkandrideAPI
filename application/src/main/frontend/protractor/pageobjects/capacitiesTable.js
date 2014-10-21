"use strict";

module.exports = function () {
    var spec = {};
    var that = {};

    spec.capacityTypes = element.all(by.css(".wdCapacityType"));

    spec.getVisibleText = function(el) {
        return el.filter(function(e) { return e.isDisplayed(); }).getText();
    };

    spec.getTypeProperty = function(type, property) {
        return spec.getVisibleText($('.wd' + type + property));
    };

    that.getTypes = function() {
        return spec.getVisibleText(spec.capacityTypes);
    };

    that.getBuilt = function(type) {
        return spec.getTypeProperty(type, 'built');
    };

    that.getUnavailable = function(type) {
        return spec.getTypeProperty(type, 'unavailable');
    };

    return that;
};