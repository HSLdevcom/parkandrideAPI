'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('manage contacts', function () {

    var contactPage = po.contactPage({});
    var editModal = contactPage.editModal;

    it('should reset data', function() {
        devApi.resetAll();
        contactPage.get();
    });

    it('should create new contact', function() {
        contactPage.openCreateModal();
        editModal.isDisplayed();
        editModal.setName("HSL");
        editModal.setPhone("(09) 4766 4444");
        editModal.setEmail("hsl@hsl.fi");
        editModal.setStreetAddress("Opastinsilta 6 A");
        editModal.setPostalCode("00077");
        editModal.setCity("Helsinki");
        editModal.setOpeningHours("ma-pe 7-19, la-su 9-17");
        editModal.setInfo("Vieraiden vastaanotto on toisessa kerroksessa.");
        editModal.save();
    });

    it('should list created contact', function() {
        expect(contactPage.getContacts()).toEqual([["HSL", "09 47664444", "hsl@hsl.fi"]]);
    });

    it('should edit contact with errors', function() {
        contactPage.openEditDialog(1);
        editModal.isDisplayed();
        editModal.setPhone("");
        editModal.setEmail("");
        editModal.save();
        expect(editModal.getViolations()).toEqual([{
            path: "Yhteystieto", message: "anna vähintään puhelinnumero tai sähköposti"
        }]);
    });

    it('should show international phone with country code', function() {
        editModal.setPhone("+44 (0)343 222 2222");
        editModal.save();
        expect(contactPage.getContacts()).toEqual([["HSL", "+44 343 222 2222", ""]]);
    });
});