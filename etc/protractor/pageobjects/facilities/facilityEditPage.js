'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var _ = require('lodash');

    that.portEditModal = require('./portEditModal')({});
    that.contactEditModal = require('../contacts/contactEditModal')({});
    that.operatorEditModal = require('../operators/operatorEditModal')({});

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
    spec.services = element(by.model('editCtrl.facility.services'));
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

    spec.paymentMethods = element(by.model('editCtrl.facility.paymentInfo.paymentMethods'));
    spec.defineMultilingualAccessors("paymentInfoDetail");
    spec.defineMultilingualAccessors("paymentInfoUrl");

    spec.operator = element(by.name('operator'));
    spec.createOperator = $('.operator .createOperator');
    spec.selectedOperator = $('.operator .ui-select-match');

    spec.facilityStatus = element(by.model('editCtrl.facility.status'));
    spec.defineMultilingualAccessors("statusDescription");

    spec.pricingSelectAll = $("#pricingSelectAll");
    spec.pricingAddRow = $('#pricingAddRow');
    spec.pricingPasteRows = $('#pricingPasteRows');
    spec.pricingCopyRows = $('#pricingCopyRows');
    spec.pricingCopyFirst = $('#pricingCopyFirst');
    spec.pricingRemoveRows = $('#pricingRemoveRows');
    spec.pricingRows = $$('#pricing-edit tbody tr');
    spec.pricingColumns = $$('#pricing-edit tbody tr td');

    spec.unavailableCapacityColumns = $$('#unavailable-capacities tbody tr td');

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
        if (contact.operator) {
            that.contactEditModal.selectOperator(contact.operator);
        }
        that.contactEditModal.save();
        that.contactEditModal.waitUntilAbsent();
    };

    that.selectEmergencyContact = function(name) {
        spec.select(spec.emergencyContact, name);
    };

    that.getEmergencyContact = function() {
        return spec.selectedEmergencyContact.getText();
    };

    that.selectOperatorContact = function(name) {
        spec.select(spec.operatorContact, name);
    };

    that.getOperatorContact = function() {
        return spec.selectedOperatorContact.getText();
    };

    that.selectServiceContact = function(name) {
        spec.select(spec.serviceContact, name);
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

    that.createOperator = function(name) {
        spec.createOperator.click();
        that.operatorEditModal.setName(name);
        that.operatorEditModal.save();
    };

    that.selectOperator = function(name) {
        spec.select(spec.operator, name);
    };

    that.getOperator = function() {
        return spec.selectedOperator.getText();
    };

    that.selectStatus = function(name) {
        spec.select(spec.facilityStatus, name);
    };

    that.getStatus = function() {
        return spec.facilityStatus.element(by.css('.ui-select-match')).getText();
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
            spec.sendKeys($("input[name='builtCapacity" + capacityType + "']"), capacity);
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
    };

    that.toListView = function () {
        return spec.toListButton.click();
    };

    that.isPaymentMethodSelected = function(name) {
        return spec.isDisplayed(spec.paymentMethods.element(by.cssContainingText(".ui-select-match-item", name)));
    };

    that.selectPaymentMethod = function(name) {
        spec.paymentMethods.element(by.css('input')).click();
        var e = browser.driver.switchTo().activeElement();
        e.sendKeys(name);
        e.sendKeys(protractor.Key.ENTER);
    };

    that.removePaymentMethod = function(name) {
        spec.paymentMethods.element(by.cssContainingText(".ui-select-match-item", name))
            .element(by.css(".ui-select-match-close"))
            .click();
    };

    that.pricingSelectAll = function() {
        spec.pricingSelectAll.click();
    };

    that.pricingAddRow = function() {
        spec.pricingAddRow.click();
    };

    that.pricingPasteRows = function() {
        spec.pricingPasteRows.click();
    };

    that.pricingCopyRows = function() {
        spec.pricingCopyRows.click();
    };

    that.pricingCopyFirst = function() {
        spec.pricingCopyFirst.click();
    };

    that.pricingRemoveRows = function() {
        spec.pricingRemoveRows.click();
    };

    that.togglePricingRow = function(index) {
        spec.pricingRows.get(index).then(function(row) {
            row.element(by.model('rowSelected')).click();
        });
    };

    that.setPricingMethod = function(pricingMethod) {
        element(by.id("pricingMethod." + pricingMethod)).click();
    };

    that.setPricing = function(index, pricing) {
        spec.pricingRows.get(index).then(function(row) {
            for (var prop in pricing) {
                switch (prop) {
                    case 'capacityType':
                        spec.select(row.element(by.model('pricing.capacityType')), pricing[prop]);
                        break;
                    case 'usage':
                        spec.select(row.element(by.model('pricing.usage')), pricing[prop]);
                        break;
                    case 'maxCapacity':
                        spec.sendKeys(row.element(by.model('pricing.maxCapacity')), pricing[prop]);
                        break;
                    case 'dayType':
                        spec.select(row.element(by.model('pricing.dayType')), pricing[prop]);
                        break;
                    case 'is24h':
                        row.element(by.model('h24')).click();
                        break;
                    case 'from':
                        spec.sendKeys(row.element(by.model('pricing.time.from')), pricing[prop]);
                        break;
                    case 'until':
                        spec.sendKeys(row.element(by.model('pricing.time.until')), pricing[prop]);
                        break;
                    case 'isFree':
                        row.element(by.model('free')).click();
                        break;
                    case 'priceFi':
                        spec.sendKeys(row.element(by.model('pricing.price.fi')), pricing[prop]);
                        break;
                    case 'priceSv':
                        spec.sendKeys(row.element(by.model('pricing.price.sv')), pricing[prop]);
                        break;
                    case 'priceEn':
                        spec.sendKeys(row.element(by.model('pricing.price.en')), pricing[prop]);
                        break;
                }
            }
        });
    };

    that.getPricingCount = function() {
        return spec.pricingColumns.count().then(function(count) {
            return count / 13;
        });
    };

    that.getPricing = function() {
        return spec.pricingColumns.then(function(columns) {
            return protractor.promise.all(_.map(columns, getColumnValue));
        }).then(getPricingRows);

        function getColumnValue(column) {
            return ifSelect()
                .then(null, ifTextarea)
                .then(null, ifInput)
                .then(null, otherwiseGetText);

            function ifSelect() {
                return column.element(by.css(".ui-select-match")).then(
                    function(selected) {
                        return selected.getText();
                    });
            }
            function ifTextarea() {
                return column.element(by.css("textarea")).then(
                    function(textarea) {
                        return textarea.getAttribute("value");
                    });
            }
            function ifInput() {
                return column.element(by.css("input")).then(
                    function(input) {
                        return input.getAttribute('type').then(function(type) {
                            if (type === "checkbox") {
                                return input.isSelected();
                            } else {
                                return input.getAttribute('value');
                            }
                        });
                    });
            }
            function otherwiseGetText() {
                return column.getText();
            }
        }

        function getPricingRows(columnValues) {
            var pricing = [];
            for (var i=0; i < columnValues.length; i+=13) {
                pricing.push({
                    selected: columnValues[i+1],
                    capacityType: columnValues[i+2],
                    usage: columnValues[i + 3],
                    maxCapacity: columnValues[i + 4],

                    dayType: columnValues[i + 5],
                    is24h: columnValues[i + 6],
                    from: columnValues[i + 7],
                    until: columnValues[i + 8],

                    isFree: columnValues[i + 9],
                    priceFi: columnValues[i + 10],
                    priceSv: columnValues[i + 11],
                    priceEn: columnValues[i + 12]
                });
            }
            return pricing;
        }

    };

    that.getUnavailableCapacitiesCount = function() {
        return spec.unavailableCapacityColumns.count().then(function(count) {
            return count / 3;
        });
    };

    that.getUnavailableCapacities = function() {
        return spec.unavailableCapacityColumns.then(function(columns) {
            return protractor.promise.all(_.map(columns, function(column) {
                return column.element(by.css("input")).then(
                    function(input) {
                        return input.getAttribute("value");
                    },
                    function() {
                        return column.getText();
                    }
                )
            }));
        }).then(function(columnValues) {
            var rows = [];
            for (var i=0; i < columnValues.length; i+=3) {
                rows.push({
                    capacityType: columnValues[i],
                    usage: columnValues[i + 1],
                    capacity: columnValues[i + 2]
                });
            }
            return rows;
        });
    };


    return that;
};