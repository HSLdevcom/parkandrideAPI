// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

'use strict';

var _ = require('lodash');
var moment = require('moment');

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var devApi = require('../devApi')();
var common = require('../common');

var arrayAssert = require('../arrayAssert')();

var defaults = {
    interval: '60 min',
    capacityTypes: ['Henkilöauto']
};

describe('reports', function () {
    var reportPage = po.reportsPage({});
    var authModal = po.authModal({});

    var smoothOperator, roughOperator;
    var smoothFacility, roughFacility, keravaFacility;
    var westendHub, keravaHub;

    beforeAll(function() {
        westendHub = fixtures.hubsFixture.westend.copy();
        keravaHub = fixtures.hubsFixture.westend.copy();
        keravaHub.id = 2;
        keravaHub.name = "Kerava";
        keravaHub.location = {
            "type": "Point",
            "coordinates": [25.106506, 60.404743]
        };
        keravaHub.facilityIds = [3];

        smoothOperator = fixtures.hubsFixture.operator;
        roughOperator = {
            id: 2,
            name: { fi: "rough operator", sv: "rough operator", en: "rough operator" },
            toPayload: function() {
                return this;
            }
        };
        smoothFacility = fixtures.facilitiesFixture.westend1.copy();
        roughFacility = fixtures.facilitiesFixture.westend2.copy();
        roughFacility.operatorId = roughOperator.id;

        keravaFacility = fixtures.facilitiesFixture.westend2.copy();
        keravaFacility.id = 3;
        keravaFacility.name = 'Kerava';
        keravaFacility.operatorId = smoothOperator.id;
        keravaFacility.location = {
            bbox: [ 25.10700775729584, 60.404853084789295, 25.108145013918158, 60.406357861238064 ],
            type: "Polygon",
            coordinates: [[
                [25.107587114443056, 60.406357861238064],
                [25.108145013918158, 60.4063366676867],
                [25.107651487459417, 60.404853084789295],
                [25.10700775729584,  60.404884876560544],
                [25.107587114443056, 60.406357861238064]
            ]]
        };

        devApi.resetAll({
            facilities: [smoothFacility, roughFacility, keravaFacility],
            hubs: [westendHub, keravaHub],
            contacts: [fixtures.hubsFixture.contact],
            operators: [smoothOperator, roughOperator]
        });
    });

    describe('hubs and facilities', function() {
        beforeAll(function() {
            devApi.loginAs('ADMIN');
            reportPage.get();
        });

        it('should select CAR, 60min by default, leave others empty', function() {
            expect(reportPage.interval.getValue()).toEqual(defaults.interval);
            expect(reportPage.capacities.getValue()).toEqual(defaults.capacityTypes);
        });

        it('should leave other fields empty', function() {
            reportPage.operators.checkEmpty();
            reportPage.usages.checkEmpty();
            reportPage.regions.checkEmpty();
            reportPage.hubs.checkEmpty();
            reportPage.facilities.checkEmpty();
        });

        it('should use first day of current month as start date', function() {
            expect(reportPage.startDate.getValue())
                .toEqual(moment().startOf('month').format('D.M.YYYY'));
        });

        it('should use current date as end date', function() {
            expect(reportPage.endDate.getValue())
                .toEqual(moment().format('D.M.YYYY'));
        });

        it('should change start date to be at most end date', function() {
            reportPage.endDate.open().verifyOpen().clickPreviousMonth().clickPreviousMonth()
                .selectDate('28');

            expect(reportPage.startDate.getValue())
                .toEqual(moment().subtract(2, 'months').date(28).format('D.M.YYYY'));
            expect(reportPage.endDate.getValue())
                .toEqual(moment().subtract(2, 'months').date(28).format('D.M.YYYY'));
        });

        it('should change end date to be at least start date', function() {

            reportPage.startDate.open().verifyOpen().clickNextMonth().selectDate('15');

            expect(reportPage.startDate.getValue())
                .toEqual(moment().subtract(1, 'months').date(15).format('D.M.YYYY'));
            expect(reportPage.endDate.getValue())
                .toEqual(moment().subtract(1, 'months').date(15).format('D.M.YYYY'));
        });
    });

    describe('filtering', function() {
        beforeAll(function() {
            devApi.loginAs('ADMIN');
            reportPage.get();
        });

        beforeAll(function() {
            reportPage.facilities.clearSelections();
            arrayAssert.assertInAnyOrder(
                reportPage.facilities.getChoices(),
                [smoothFacility.name, roughFacility.name, keravaFacility.name]
            );
            reportPage.facilities.select(smoothFacility.name);
            reportPage.facilities.select(roughFacility.name);
            reportPage.facilities.select(keravaFacility.name);
            expect(reportPage.facilities.getValue())
                .toEqual([smoothFacility.name, roughFacility.name, keravaFacility.name]);
        });

        it('should grey out facilities when filtered out by operator', function() {
            expect(reportPage.facilities.getGreyedOutSelections()).toEqual([]);
            reportPage.operators.select(roughOperator.name.fi);
            expect(reportPage.facilities.getGreyedOutSelections())
                .toEqual([smoothFacility.name, keravaFacility.name]);
            reportPage.operators.clearSelections();
        });

        it('should grey out facilities when filtered out by hub', function() {
            reportPage.hubs.select(westendHub.name);
            arrayAssert.assertInAnyOrder(
                reportPage.facilities.getGreyedOutSelections(),
                [keravaFacility.name]
            );
            reportPage.hubs.clearSelections();
        });

        it('should grey out facilities when filtered out by region', function() {
            reportPage.regions.select('Espoo');
            arrayAssert.assertInAnyOrder(
                reportPage.facilities.getGreyedOutSelections(),
                [keravaFacility.name]
            );
            reportPage.regions.clearSelections();
        });
    });

    describe('as admin', function() {
        beforeAll(function() {
            devApi.loginAs('ADMIN');
            reportPage.get();
        });

        it('should show the page', function() {
            expect(reportPage.isDisplayed()).toBe(true);
        });

        it('should show operator search input', function() {
            reportPage.checkOperatorNotFixed();
        });
    });

    describe('as operator', function() {
        var operator;
        beforeAll(function() {
            operator = smoothOperator;
            devApi.logout();
            devApi.loginAs('OPERATOR','ope','Ope1!', operator.id);
            reportPage.get();
        });

        it('should show the page', function() {
            expect(reportPage.isDisplayed()).toBe(true);
        });

        it('the operator selection should be locked', function() {
            reportPage.checkOperatorFixedTo(operator.name.fi);
        });

        it('should not show other operator\'s facilities', function() {
            arrayAssert.assertInAnyOrder(
                reportPage.facilities.getChoices(),
                [smoothFacility.name]
            );
        });
    });

    describe('when not logged in', function() {
        beforeAll(function() {
            devApi.logout();
            reportPage.get();
        });

        it('should show the auth dialog', function() {
            expect(authModal.isDisplayed()).toBe(true);
        });
    });

});