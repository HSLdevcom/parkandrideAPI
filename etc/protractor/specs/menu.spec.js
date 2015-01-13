'use strict';

var _ = require('lodash');

var po = require('../pageobjects/pageobjects.js');
var fixtures = require('../fixtures/fixtures');
var devApi = require('./devApi')();
var common = require('./common');

describe('menu', function () {
    var indexPage = po.indexPage({});
    var menu = po.menu({});
    var hubEditPage = po.hubEditPage({});
    var hubViewPage = po.hubViewPage({});
    var facilityEditPage = po.facilityEditPage({});
    var facilityViewPage = po.facilityViewPage({});
    var contactPage = po.contactPage({});
    var operatorPage = po.operatorPage({});

    var h = fixtures.hubsFixture.westend;

    function assertTabs(isHubsTabActive, isContactsTabActive, isOperatorsTabActive) {
        expect(menu.isHubsActive()).toBe(isHubsTabActive);
        expect(menu.isContactsActive()).toBe(isContactsTabActive);
        expect(menu.isOperatorsActive()).toBe(isOperatorsTabActive);
    }

    function assertHubsTabActive() {
        assertTabs(true, false, false);
    }

    function assertContactsTabActive() {
        assertTabs(false, true, false);
    }

    function assertOperatorsTabActive() {
        assertTabs(false, false, true);
    }

    beforeEach(function () {
        devApi.resetAll({facilities: h.facilities, hubs: [h], contacts: [fixtures.hubsFixture.contact], operators: [fixtures.hubsFixture.operator]});
        devApi.loginAs('ADMIN');

        indexPage.get();
        assertHubsTabActive();
    });

    it('click based tab navigation', function() {
        menu.toContacts();
        assertContactsTabActive();

        menu.toOperators();
        assertOperatorsTabActive();

        menu.toHubs();
        assertHubsTabActive();
    });

    describe('inactive hubs tab is activated', function() {
        beforeEach(function () {
            contactPage.get();
            assertContactsTabActive();
        });

        it('when hub create is accessed with direct url', function() {
            hubEditPage.get();
            assertHubsTabActive();
        });

        it('when hub edit is accessed with direct url', function() {
            hubEditPage.get(h.facilities[0].id);
            assertHubsTabActive();
        });

        it('when hub view is accessed with direct url', function() {
            hubViewPage.get(h.facilities[0].id);
            assertHubsTabActive();
        });

        it('when facility create is accessed with direct url', function() {
            facilityEditPage.get();
            assertHubsTabActive();
        });

        it('when facility edit is accessed with direct url', function() {
            facilityEditPage.get(h.facilities[0].id);
            assertHubsTabActive();
        });

        it('when facility view is accessed with direct url', function() {
            facilityViewPage.get(h.facilities[0].id);
            assertHubsTabActive();
        });
    });

    it('inactive contacts tab is activated when accessed with direct url', function() {
        contactPage.get();
        assertContactsTabActive();
    });

    it('inactive operator tab is activated when accessed with direct url', function() {
        operatorPage.get();
        assertOperatorsTabActive();
    });
});