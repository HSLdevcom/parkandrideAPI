'use strict';

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var devApi = require('../devApi')();

describe('edit facility view', function () {
    var editPage = po.facilityEditPage({});

    describe('new facility', function () {
        beforeEach(function () {
            devApi.resetAll();
            editPage.get();
        });

        it('initially no errors exist', function () {
            expect(editPage.isNameFiRequiredError()).toBe(false);
            expect(editPage.isNameSvRequiredError()).toBe(false);
            expect(editPage.isNameEnRequiredError()).toBe(false);
            expect(editPage.isFacilityRequiredError()).toBe(false);
        });

        it('required error is shown only for edited fields', function () {
            editPage.setNameFi("foo");
            editPage.setNameFi("");
            editPage.setNameEn("bar");
            expect(editPage.isNameFiRequiredError()).toBe(true);
            expect(editPage.isNameSvRequiredError()).toBe(false);
            expect(editPage.isNameEnRequiredError()).toBe(false);
            expect(editPage.isFacilityRequiredError()).toBe(false);
        });

        it('required errors are shown for all required fields if user submits empty form without editing', function() {
            editPage.save();
            expect(editPage.isNameFiRequiredError()).toBe(true);
            expect(editPage.isNameSvRequiredError()).toBe(true);
            expect(editPage.isNameEnRequiredError()).toBe(true);
            expect(editPage.isFacilityRequiredError()).toBe(true);
        });

        describe('name', function () {
            it('is required in all languages', function () {
                editPage.setName("facility name");
                editPage.setNameFi("");
                expect(editPage.isNameFiRequiredError()).toBe(true);
                expect(editPage.isNameSvRequiredError()).toBe(false);
                expect(editPage.isNameEnRequiredError()).toBe(false);

                editPage.setName("facility name");
                editPage.setNameSv("");
                expect(editPage.isNameFiRequiredError()).toBe(false);
                expect(editPage.isNameSvRequiredError()).toBe(true);
                expect(editPage.isNameEnRequiredError()).toBe(false);

                editPage.setName("facility name");
                editPage.setNameEn("");
                expect(editPage.isNameFiRequiredError()).toBe(false);
                expect(editPage.isNameSvRequiredError()).toBe(false);
                expect(editPage.isNameEnRequiredError()).toBe(true);
            });

            it('max length is 255', function () {
                var max = new Array(255+1).join("x");
                var tooLong = max + "y";
                editPage.setName(tooLong);
                expect(editPage.getNameFi()).toEqual(max);
                expect(editPage.getNameSv()).toEqual(max);
                expect(editPage.getNameEn()).toEqual(max);
            });
        });

        describe('facility', function () {
            it('is required', function () {
                editPage.setName("Hub name");
                editPage.save();
                expect(editPage.isFacilityRequiredError()).toBe(true);
            });
        });
    });
});
