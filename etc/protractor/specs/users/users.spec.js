"use strict";

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('users', function () {

    function create(object) {
        object.id = ++seq;
        object.toPayload = function() { return this; };

        if (object.role) {
            if (object.role === 'ADMIN') {
                object.roleFi = 'Ylläpitäjä';
                if (object.username) {
                    object.password = object.username;
                } else {
                    object.username = object.password = 'admin';
                }
            }
            if (object.role === 'OPERATOR') object.roleFi = 'Operaattori';
            if (object.role === 'OPERATOR_API') object.roleFi = 'API';

            if (object.operatorId) {
                object.operator = operators[object.operatorId];
                object.username = object.password =  object.operator.name.fi + object.id
            }
        }

        return object;
    }

    var seq = 0;

    var operatorX = create({ name: { fi: "x-operator", sv: "x-operator", en: "x-operator"}});
    var operatorY = create({ name: { fi: "y-operator", sv: "y-operator", en: "y-operator"}});
    var operators = _.indexBy([operatorX, operatorY], "id");

    var admin = create({ role: "ADMIN"});
    var operatorX_user = create({ role: "OPERATOR", operatorId: operatorX.id });
    var operatorX_api = create({ role: "OPERATOR_API", operatorId: operatorX.id});
    var operatorY_user = create({ role: "OPERATOR", operatorId: operatorY.id});

    var usersPage = po.usersPage({});
    var menu = po.menu({});
    var authModal = po.authModal({});

    function toUsersAs(user) {
        menu.toHubs(); // required for the users view to refresh on logout/login

        menu.canLogout().then(function(canLogout) { if (canLogout) menu.logout(); });
        menu.openLoginModal();
        authModal.login(user.username,  user.password);
        authModal.waitUntilAbsent();

        menu.toUsers();
        expect(usersPage.isDisplayed()).toBe(true);
    }

    function assertUser(actual, expected) {
        expect(actual.username).toEqual(expected.username);
        expect(actual.operator).toEqual(expected.operator ? expected.operator.name.fi : '');
        expect(actual.role).toEqual(expected.roleFi);
    }

    describe('navigation', function () {
        beforeEach(function () {
            devApi.loginAs(admin.role, admin.username, admin.password);
            usersPage.get();
        });

        it('to create user', function () {
            usersPage.toCreateUser();
            expect(usersPage.userModal.isDisplayed()).toBe(true);
        });

        // TODO: edit pending, let's see it this is even needed
    });

    describe('lists', function () {
        beforeEach(function () {
            devApi.resetAll({
                operators: [operatorX, operatorY],
                users: _.shuffle([operatorX_user, operatorX_api, operatorY_user])
            });
            devApi.loginAs(admin.role, admin.username, admin.password);
            usersPage.get();
            expect(usersPage.isDisplayed()).toBe(true);
        });

        it('by name, role and username', function () {
            usersPage.getUsers().then(function (actual){
                assertUser(actual[0], admin);
                assertUser(actual[1], operatorX_user);
                assertUser(actual[2], operatorX_api);
                assertUser(actual[3], operatorY_user);
            });
        });

        it("operator does not see admin nor other operator's users", function() {
            toUsersAs(operatorX_user);
            usersPage.getUsers().then(function (actual){
                expect(actual.length).toBe(2);
                assertUser(actual[0], operatorX_user);
                assertUser(actual[1], operatorX_api);
            });
        });
    });

    describe('create as admin', function () {

        beforeEach(function () {
            devApi.resetAll({
                operators: [operatorX, operatorY],
                users: _.shuffle([operatorX_user, operatorX_api, operatorY_user])
            });
            devApi.loginAs(admin.role, admin.username, admin.password);
            usersPage.get();
            usersPage.toCreateUser();
            expect(usersPage.userModal.isDisplayed()).toBe(true);
        });

        it('on enter all fields are shown', function () {
            expect(usersPage.userModal.getRoles()).toEqual(['Ylläpitäjä', 'Operaattori', 'API']);
            expect(usersPage.userModal.canSetUsername()).toBe(true);
            expect(usersPage.userModal.canSetPassword()).toBe(true);
            expect(usersPage.userModal.getOperators()).toEqual([operatorX.name.fi, operatorY.name.fi]);
        });

        it('when admin role is selected, operator is hidden', function () {
            expect(usersPage.userModal.canSetOperator()).toBe(true);
            usersPage.userModal.setRole('Ylläpitäjä');
            expect(usersPage.userModal.canSetOperator()).toBe(false);
        });

        it('when api role is selected, password is hidden', function () {
            expect(usersPage.userModal.canSetPassword()).toBe(true);
            usersPage.userModal.setRole('API');
            expect(usersPage.userModal.canSetPassword()).toBe(false);
        });

        it('added user is shown on the list', function () {
            var newadmin = create({ role: "ADMIN", username: "newadmin"});

            usersPage.userModal.setRole('Ylläpitäjä');
            usersPage.userModal.setUsername(newadmin.username);
            usersPage.userModal.setPassword(newadmin.password);
            usersPage.userModal.save();

            usersPage.userModal.isDisplayed(false);
            usersPage.getUsers().then(function (actual){
                assertUser(actual[0], admin);
                assertUser(actual[1], newadmin);
                assertUser(actual[2], operatorX_user);
                assertUser(actual[3], operatorX_api);
                assertUser(actual[4], operatorY_user);
            });
        });
    });

    describe('create as operator', function () {
        beforeEach(function () {
            devApi.resetAll({
                operators: [operatorX, operatorY],
                users: _.shuffle([admin, operatorX_user, operatorX_api, operatorY_user])
            });
            devApi.loginAs(operatorX_user.role, operatorX_user.username, operatorX_user.password);
            usersPage.get();
            usersPage.toCreateUser();
            expect(usersPage.userModal.isDisplayed()).toBe(true);
        });

        it('on enter operator is fixed', function () {
            expect(usersPage.userModal.getOperators()).toEqual([operatorX.name.fi]);
        });

        it('cannot create admin', function () {
            expect(usersPage.userModal.getRoles()).toEqual(['Operaattori', 'API']);
        });

        it('can create operator', function () {
            var newoperator = create({ role: "OPERATOR", username: "newoperator", operatorId: operatorX.id});

            usersPage.userModal.setRole('Operaattori');
            usersPage.userModal.setUsername(newoperator.username);
            usersPage.userModal.setPassword(newoperator.password);
            usersPage.userModal.save();

            usersPage.userModal.isDisplayed(false);

            usersPage.getUsers().then(function (actual){
                expect(actual.length).toBe(3);
                assertUser(actual[0], operatorX_user);
                assertUser(actual[1], newoperator);
                assertUser(actual[2], operatorX_api);
            });
        });

        it('can create operator api', function () {
            var newoperator = create({ role: "OPERATOR_API", username: "newoperatorapi", operatorId: operatorX.id});

            usersPage.userModal.setRole('API');
            usersPage.userModal.setUsername(newoperator.username);
            usersPage.userModal.save();

            usersPage.userModal.isDisplayed(false);

            usersPage.getUsers().then(function (actual){
                expect(actual.length).toBe(3);
                assertUser(actual[0], operatorX_user);
                assertUser(actual[1], operatorX_api);
                assertUser(actual[2], newoperator);
            });

        });
    });

    describe('delete', function () {
        beforeEach(function () {
            devApi.resetAll({
                operators: [operatorX, operatorY],
                users: _.shuffle([operatorX_user])
            });
            devApi.loginAs(admin.role, admin.username, admin.password);
            usersPage.get();
        });

        it('cannot delete itself', function () {
            usersPage.getUsers().then(function (actual) {
                assertUser(actual[0], admin);
                assertUser(actual[1], operatorX_user);
            });

            expect(usersPage.canBeDeleted(0)).toBe(false);
            expect(usersPage.canBeDeleted(1)).toBe(true);
        });

        it('delete is confirmed and can be cancelled', function () {
            usersPage.delete(1);
            expect(usersPage.confirmModal.isDisplayed()).toBe(true);

            usersPage.confirmModal.cancel();
            expect(usersPage.confirmModal.isDisplayed()).toBe(false);

            usersPage.getUsers().then(function (actual) {
                assertUser(actual[0], admin);
                assertUser(actual[1], operatorX_user);
            });
        });

        it('after delete, the user list is updated accordingly', function () {
            usersPage.delete(1);
            expect(usersPage.confirmModal.isDisplayed()).toBe(true);

            usersPage.confirmModal.confirm();
            expect(usersPage.confirmModal.isDisplayed()).toBe(false);

            usersPage.getUsers().then(function (actual) {
                expect(actual.length).toBe(1);
                assertUser(actual[0], admin);
            });
        });
    });

});