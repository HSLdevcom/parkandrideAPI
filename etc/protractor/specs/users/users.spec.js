"use strict";

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('users', function () {

    function create(object) {
        return _.assign(object, { id: ++seq, toPayload: function() { return this; }});
    }
    var seq = 0;
    var operator = create;
    var user = create;

    var operatorX = operator({ name: { fi: "x-operator", sv: "x-operator", en: "x-operator"}});
    var operatorY = operator({ name: { fi: "y-operator", sv: "y-operator", en: "y-operator"}});

    var admin = user({ username: "admin", password: "admin_pass", role: "ADMIN"});
    var operatorX_user = user({ username: "operatorx_user", password: "operatorX_user_pass", role: "OPERATOR", operatorId: operatorX.id });
    var operatorX_api = user({ username: "operatorx_api", role: "OPERATOR_API", operatorId: operatorX.id});
    var operatorY_user = user({ username: "operatory_user", password: "operatorY_user_pass", role: "OPERATOR", operatorId: operatorY.id});

    var usersPage = po.usersPage({});

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
                users: [operatorX_user, operatorX_api, operatorY_user]
            });
            devApi.loginAs(admin.role, admin.username, admin.password);
            usersPage.get();
        });

        it('by name, role and username', function () {
            expect(usersPage.isDisplayed()).toBe(true);

            usersPage.getUsers().then(function (actual){
                expect(actual[0].username).toEqual(admin.username);
                expect(actual[0].operator).toEqual("");
                expect(actual[0].role).toEqual("admin");

                expect(actual[1].username).toEqual(operatorX_user.username);
                expect(actual[1].operator).toEqual(operatorX.name.fi);
                expect(actual[1].role).toEqual("operaattori");

                expect(actual[2].username).toEqual(operatorX_api.username);
                expect(actual[2].operator).toEqual(operatorX.name.fi);
                expect(actual[2].role).toEqual("API");

                expect(actual[3].username).toEqual(operatorY_user.username);
                expect(actual[3].operator).toEqual(operatorY.name.fi);
                expect(actual[3].role).toEqual("operaattori");
            });
        });
    });
});