'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('manage contacts', function () {

    var contactPage = po.contactPage({});
    var editModal = contactPage.editModal;

    it('should login as admin', function() {
        devApi.resetAll();
        devApi.loginAs('ADMIN');
    });

    it('no errors shown initially', function() {
        contactPage.get();
        contactPage.openCreateModal();
        expect(editModal.isNameFiRequiredError()).toBe(false);
        expect(editModal.isNameSvRequiredError()).toBe(false);
        expect(editModal.isNameEnRequiredError()).toBe(false);
    });

    it('errors shown on save', function() {
        editModal.save();
        expect(editModal.isDisplayed()).toBe(true);
        expect(editModal.getViolations()).toEqual([{ path: "Yhteystieto", message: "tarkista pakolliset tiedot ja syötteiden muoto" }]);
        expect(editModal.isNameFiRequiredError()).toBe(true);
        expect(editModal.isNameSvRequiredError()).toBe(true);
        expect(editModal.isNameEnRequiredError()).toBe(true);
    });

    it('should create new contact', function() {
        expect(editModal.isDisplayed()).toBe(true);
        editModal.setName("HSL");
        editModal.setPhone("+44 343 222 2222");
        editModal.setEmail("hsl@hsl.fi");
        editModal.setStreetAddress("Opastinsilta 6 A");
        editModal.setPostalCode("00077");
        editModal.setCity("Helsinki");
        editModal.setOpeningHours("ma-pe 7-19, la-su 9-17");
        editModal.setInfo("Vieraiden vastaanotto on toisessa kerroksessa.");
        editModal.save();
        editModal.waitUntilAbsent();
        expect(editModal.isDisplayed()).toBe(false);
    });

    it('should list created contact', function() {
        expect(contactPage.getContacts()).toEqual([["HSL", "+44 343 222 2222", "hsl@hsl.fi"]]);
    });

    it('should require phone or email', function() {
        contactPage.openEditDialog(1);
        expect(editModal.isDisplayed()).toBe(true);
        editModal.setPhone("");
        editModal.setEmail("");
        editModal.save();
        expect(editModal.isDisplayed()).toBe(true);
        expect(editModal.getViolations()).toEqual([{
            path: "Yhteystieto", message: "anna vähintään puhelinnumero tai sähköposti"
        }]);
    });

    it('should change phone number and allow empty email', function() {
        editModal.setPhone("(09) 4766 4444");
        expect(editModal.getEmail()).toBe("");
        editModal.save();
        editModal.waitUntilAbsent();
        expect(editModal.isDisplayed()).toBe(false);
        expect(contactPage.getContacts()).toEqual([["HSL", "09 47664444", ""]]);
    });

    it('should cancel edits', function() {
        contactPage.openEditDialog(1);
        expect(editModal.isDisplayed()).toBe(true);
        editModal.setPhone("+44 343 222 2222");
        editModal.setEmail("hsl@hsl.fi");
        editModal.cancel();
        editModal.waitUntilAbsent();
        expect(editModal.isDisplayed()).toBe(false);
        expect(contactPage.getContacts()).toEqual([["HSL", "09 47664444", ""]]);
    });
});