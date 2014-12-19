'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    that.portEditModal = require('./portEditModal')({});
    that.contactEditModal = require('../contacts/contactEditModal')({});

    spec.view = $('.wdFacilityEditView');
    spec.map = $('.facility-map .ol-viewport');
    spec.saveButton = element.all(by.css('.wdSave')).first();
    spec.aliases = $('.wdAliases .ui-select-multiple');
    spec.capacityTypes = element.all(by.css(".wdCapacityType"));
    spec.form = $('form');
    spec.toListButton = element.all(by.linkUiSref('hub-list')).first();
    spec.zoomIn = element(by.css('button.ol-zoom-in'));
    spec.zoomOut = element(by.css('button.ol-zoom-in'));
    spec.editModePorts = element(by.id('editModePorts'));
    spec.editModeLocation = element(by.id('editModeLocation'));
    spec.services = element(by.model('editCtrl.facility.serviceIds'));
    spec.emergencyContact = element(by.name('emergencyContact'));
    spec.operatorContact = element(by.name('operatorContact'));
    spec.serviceContact = element(by.name('serviceContact'));
    spec.createEmergencyContact = $('.emergencyContact .createContact');
    spec.selectedEmergencyContact = $('.emergencyContact .ui-select-match');
    spec.selectedOperatorContact = $('.operatorContact .ui-select-match');
    spec.selectedServiceContact = $('.serviceContact .ui-select-match');
    spec.clearEmergencyContact = $('.emergencyContact .clearContact');
    spec.clearOperatorContact = $('.operatorContact .clearContact');
    spec.clearServiceContact = $('.serviceContact .clearContact');

    spec.defineMultilingualAccessors("name");

    that.get = function (id) {
        if (id) {
            browser.get('/#/facilities/edit/' + id);
        } else {
            browser.get('/#/facilities/create');
        }
    };

    that.selectService = function(name) {
        spec.services.element(by.css('input')).click();
        var servicesElement = browser.driver.switchTo().activeElement();
        servicesElement.sendKeys(name);
        servicesElement.sendKeys(protractor.Key.ENTER);
    };

    that.removeService = function(name) {
        spec.services
            .element(by.cssContainingText(".ui-select-match-item", name))
            .element(by.css(".ui-select-match-close"))
            .click();
    };

    that.isServiceSelected = function(name) {
        return spec.services
            .element(by.cssContainingText(".ui-select-match-item", name))
            .then(
                function(elem) {
                    return elem.isDisplayed();
                },
                function() {
                    return false;
                }
            );
    };

    that.createContact = function(contact) {
        spec.createEmergencyContact.click();
        that.contactEditModal.setName(contact.name);
        that.contactEditModal.setPhone(contact.phone);
        that.contactEditModal.setEmail(contact.email);
        that.contactEditModal.save();
        that.contactEditModal.waitUntilAbsent();
    };

    that.selectEmergencyContact = function(name) {
        spec.emergencyContact.element(by.css('.ui-select-match')).click();
        var contactElement = browser.driver.switchTo().activeElement();
        contactElement.sendKeys(name);
        contactElement.sendKeys(protractor.Key.ENTER);
    };

    that.getEmergencyContact = function() {
        return spec.selectedEmergencyContact.getText();
    };

    that.selectOperatorContact = function(name) {
        spec.operatorContact.element(by.css('.ui-select-match')).click();
        var contactElement = browser.driver.switchTo().activeElement();
        contactElement.sendKeys(name);
        contactElement.sendKeys(protractor.Key.ENTER);
    };

    that.getOperatorContact = function() {
        return spec.selectedOperatorContact.getText();
    };

    that.selectServiceContact = function(name) {
        spec.serviceContact.element(by.css('.ui-select-match')).click();
        var contactElement = browser.driver.switchTo().activeElement();
        contactElement.sendKeys(name);
        contactElement.sendKeys(protractor.Key.ENTER);
    };

    that.getServiceContact = function() {
        return spec.selectedServiceContact.getText();
    };

    that.clearEmergencyContact = function() {
        spec.clearEmergencyContact.click();
    };

    that.clearOperatorContact = function() {
        spec.clearOperatorContact.click();
    };

    that.clearServiceContact = function() {
        spec.clearServiceContact.click();
    };

    that.drawLocation = function (topLeft, w, h) {
        spec.editModeLocation.click();
        browser.actions()
            .mouseMove(spec.map, topLeft).click()
            .mouseMove(spec.map, {x: topLeft.x, y: topLeft.y + h}).click()
            .mouseMove(spec.map, {x: topLeft.x + w, y: topLeft.y + h}).click()
            .mouseMove(spec.map, {x: topLeft.x + w, y: topLeft.y}).click()
            .mouseMove(spec.map, topLeft).click()
            .perform();
        // Sleep to prevent interfering with other clicks
        browser.sleep(200);
    };

    that.save = function () {
        spec.saveButton.click();
    };

    that.addAlias = function (alias) {
        spec.aliases.click();
        var tagsElement = browser.driver.switchTo().activeElement();
        tagsElement.sendKeys(alias);
        tagsElement.sendKeys(protractor.Key.ENTER);
    };

    that.setCapacities = function (capacities, doBlur) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                $("input[name='" + capacityType + prop + "']").sendKeys(capacity[prop]);
            }
        }

        if (doBlur) {
            spec.nameFi.click();
        } 
    };

    that.getCapacityTypes = function() {
      return spec.capacityTypes.filter(function(el) { return el.isDisplayed(); }).getText();
    };

    that.isFacilityRequiredError = function() {
        return spec.isRequiredError($('facility-map'));
    };

    that.zoom = function(level) {
        if (level > 0) {
            for (var i=0; i < level; i++) {
                spec.zoomIn.click();
            }
        } else {
            for (var i=0; i > level; i--) {
                spec.zoomOut.click();
            }
        }
    };

    that.openPortAt = function(x, y) {
        spec.editModePorts.click();
        browser.actions()
            .mouseMove(spec.map, {x: x, y: y}).click().click().perform();
        // Sleep to prevent interfering with other clicks
        browser.sleep(200);
    }

    that.toListView = function () {
        return spec.toListButton.click();
    };

    return that;
};