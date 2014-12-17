'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var devApi = require('../devApi')();
var common = require('../common');

describe('authorization', function () {

    var authModal = po.authModal();
    var contactPage = po.contactPage({});
    var contactEditModal = contactPage.editModal;

    var password = 'very secret password';
    var username = 'testuser';

    it('should got to main page', function() {
        browser.get('/');
    });

    describe('login and logout buttons', function() {

        beforeEach(function() {
            devApi.softLogout();
        });

        it('should reset testuser', function () {
            devApi.loginAs('ADMIN', username, password);
        });

        it('should show login error for wrong password', function() {
            expect(authModal.isDisplayed()).toBe(false);
            expect(authModal.isLogoutDisplayed()).toBe(false);

            authModal.openLoginModal();
            authModal.login(username, "wrong");

            expect(authModal.isDisplayed()).toBe(true);
            expect(authModal.isLoginError()).toBe(true);
            expect(authModal.getUsername()).toBe(username);
        });

        it('should show login error for wrong username', function() {
            browser.get('/');

            authModal.openLoginModal();
            authModal.login("wrong", password);

            expect(authModal.isDisplayed()).toBe(true);
            expect(authModal.isLoginError()).toBe(true);
        });

        it('should login and logout', function() {
            browser.get('/');

            authModal.openLoginModal();
            authModal.login(username, password);

            expect(authModal.isDisplayed()).toBe(false);
            expect(authModal.isLogoutDisplayed()).toBe(true);

            authModal.logout();

            expect(authModal.isLoginDisplayed()).toBe(true);
        });
    });

    it('should require login on submit', function() {
        devApi.loginAs('ADMIN', username, password);

        contactPage.get();
        contactPage.openCreateModal();
        contactEditModal.setName("HSL");
        contactEditModal.setPhone("(09) 4766 4444");

        devApi.softLogout();
        contactEditModal.save();

        expect(authModal.isDisplayed()).toBe(true);
        expect(contactEditModal.isDisplayed()).toBe(true);

        authModal.login(username, password);

        expect(authModal.isDisplayed()).toBe(false);
        expect(contactEditModal.isDisplayed()).toBe(false);

        expect(authModal.isLogoutDisplayed()).toBe(true);
    });
});