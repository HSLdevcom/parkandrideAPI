'use strict';

var Menu = (function() {
    function Menu() {
        this.facilities = element(by.linkUiSref('facility-list'));
        this.hubs = element(by.linkUiSref('hub-list'));
    }

    Menu.prototype.selectFacilities = function() {
        return this.facilities.click();
    };

    Menu.prototype.selectHubs = function() {
        return this.hubs.click();
    };

    return Menu;
})();

module.exports = Menu;