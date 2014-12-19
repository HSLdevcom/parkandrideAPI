'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('manage operators', function () {

    var operatorPage = po.operatorPage({});
    var editModal = operatorPage.editModal;

    it('should login as admin', function() {
        devApi.loginAs('ADMIN');
    });

    it('should reset data', function() {
        operatorPage.get();
        devApi.resetAll();
    });

    it('no errors shown initially', function() {
        operatorPage.openCreateModal();
        expect(editModal.isNameFiRequiredError()).toBe(false);
        expect(editModal.isNameSvRequiredError()).toBe(false);
        expect(editModal.isNameEnRequiredError()).toBe(false);
    });

    it('errors shown on save', function() {
        editModal.save();
        expect(editModal.getViolations()).toEqual([{ path: "Operaattori", message: "tarkista pakolliset tiedot ja syötteiden muoto" }]);
        expect(editModal.isNameFiRequiredError()).toBe(true);
        expect(editModal.isNameSvRequiredError()).toBe(true);
        expect(editModal.isNameEnRequiredError()).toBe(true);
    });

    it('should create new operator', function() {
        expect(editModal.isDisplayed()).toBe(true);
        editModal.setName("Operator");
        editModal.save();
        editModal.waitUntilAbsent();
    });

    it('should list created operator', function() {
        expect(operatorPage.getOperators()).toEqual([["Operator"]]);
    });

    it('should edit name', function() {
        operatorPage.openEditDialog(1);
        expect(editModal.isDisplayed()).toBe(true);
        editModal.setName("Smooth operator");
        editModal.save();
        editModal.waitUntilAbsent();
        expect(operatorPage.getOperators()).toEqual([["Smooth operator"]]);
    });

    it('should cancel edits', function() {
        operatorPage.openEditDialog(1);
        expect(editModal.isDisplayed()).toBe(true);
        editModal.setName("");
        editModal.cancel();
        editModal.waitUntilAbsent();
        expect(editModal.isDisplayed()).toBe(false);
        expect(operatorPage.getOperators()).toEqual([["Smooth operator"]]);
    });
});