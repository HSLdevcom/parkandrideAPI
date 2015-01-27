'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var devApi = require('../devApi')();
var common = require('../common');

describe('authorization', function () {

    var indexPage = po.indexPage({});
    var menu = po.menu({});
    var authModal = po.authModal();
    var operatorPage = po.operatorPage({});
    var operatorEditModal = operatorPage.editModal;

    var password = 'very secret password';
    var username = 'testuser';

    function openLoginModal() {
        expect(authModal.isDisplayed()).toBe(false);
        menu.openLoginModal();
        expect(authModal.isDisplayed()).toBe(true);
    }

    beforeEach(function() {
        devApi.resetAll();
        devApi.createLogin('ADMIN', username, password);
        indexPage.get();
        expect(indexPage.isDisplayed()).toBe(true);
    });

    it('login modal can be closed', function() {
        openLoginModal();

        authModal.cancel();
        expect(authModal.isDisplayed()).toBe(false);
    });

    describe('login and logout buttons', function() {
        it('should show login error for wrong password', function() {
            openLoginModal();

            authModal.login(username, "wrong");

            expect(authModal.isDisplayed()).toBe(true);
            expect(authModal.isLoginError()).toBe(true);
            expect(authModal.getUsername()).toBe(username);
        });

        it('should show login error for wrong username', function() {
            openLoginModal();

            authModal.login("wrong", password);

            expect(authModal.isDisplayed()).toBe(true);
            expect(authModal.isLoginError()).toBe(true);
        });

        it('should login and logout', function() {
            openLoginModal();

            authModal.login(username, password);
            authModal.waitUntilAbsent();

            expect(authModal.isDisplayed()).toBe(false);

            expect(menu.canLogin()).toBe(false);
            menu.logout();
            expect(menu.canLogin()).toBe(true);
        });
    });

    xit('should require login on submit', function() {
        // XXX: Test this using a mock backend returning 401
        // 1) operatorEditModal.save() is not shown if user is not logged in
        // 2) if Session uses loginCache, we cannot logout without Angular noticing it
        // 3) waiting for the session to actually expire would take too long

        menu.openLoginModal();
        authModal.login(username, password);
        authModal.waitUntilAbsent();

        operatorPage.get();
        operatorPage.openCreateModal();
        devApi.logout();
        operatorEditModal.setName("smooth");
        operatorEditModal.save();
        expect(authModal.isDisplayed()).toBe(true);
        expect(operatorEditModal.isDisplayed()).toBe(true);

        authModal.login(username, password);

        authModal.waitUntilAbsent();
        expect(authModal.isDisplayed()).toBe(false);

        operatorEditModal.waitUntilAbsent();
        expect(operatorEditModal.isDisplayed()).toBe(false);

        expect(menu.canLogout()).toBe(true);
    });
});