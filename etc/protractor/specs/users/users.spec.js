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
            if (object.role === 'ADMIN') object.username = object.password = object.roleFi = 'admin';
            if (object.role === 'OPERATOR') object.roleFi = 'operaattori';
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

    describe('lists users', function () {
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
});