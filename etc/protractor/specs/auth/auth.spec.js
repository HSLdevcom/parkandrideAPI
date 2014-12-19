'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var devApi = require('../devApi')();
var common = require('../common');

describe('authorization', function () {

    var indexPage = po.indexPage({});
    var authModal = po.authModal();
    var contactPage = po.contactPage({});
    var contactEditModal = contactPage.editModal;

    var password = 'very secret password';
    var username = 'testuser';

    beforeEach(function() {
        devApi.createLogin('ADMIN', username, password);
        indexPage.get();
        expect(indexPage.isDisplayed()).toBe(true);
    });

    describe('login and logout buttons', function() {
        it('should show login error for wrong password', function() {
            expect(authModal.isDisplayed()).toBe(false);
            expect(authModal.isLogoutDisplayed()).toBe(false); // TODO: logout is not on auth modal

            authModal.openLoginModal();
            authModal.login(username, "wrong");

            expect(authModal.isDisplayed()).toBe(true);
            expect(authModal.isLoginError()).toBe(true);
            expect(authModal.getUsername()).toBe(username);
        });

        it('should show login error for wrong username', function() {
            authModal.openLoginModal();
            authModal.login("wrong", password);

            expect(authModal.isDisplayed()).toBe(true);
            expect(authModal.isLoginError()).toBe(true);
        });

        it('should login and logout', function() {
            authModal.openLoginModal();
            authModal.login(username, password);

            authModal.waitUntilAbsent();
            expect(authModal.isDisplayed()).toBe(false);
            expect(authModal.isLogoutDisplayed()).toBe(true); // TODO: logout is not on auth modal

            authModal.logout();
            expect(authModal.isLoginDisplayed()).toBe(true);
        });
    });

    it('should require login on submit', function() {
        contactPage.get();
        contactPage.openCreateModal();
        contactEditModal.setName("HSL");
        contactEditModal.setPhone("(09) 4766 4444");

        contactEditModal.save();
        expect(authModal.isDisplayed()).toBe(true);
        expect(contactEditModal.isDisplayed()).toBe(true);

        authModal.login(username, password);

        authModal.waitUntilAbsent();
        expect(authModal.isDisplayed()).toBe(false);

        contactEditModal.waitUntilAbsent();
        expect(contactEditModal.isDisplayed()).toBe(false);

        expect(authModal.isLogoutDisplayed()).toBe(true); // TODO: logout is not on auth modal
    });
});