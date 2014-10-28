'use strict';

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var devApi = require('../devApi')();

describe('edit hub with 2 facilities', function () {

    beforeEach(function () {
        devApi.resetAll(fixtures.facilitiesFixture.all, fixtures.hubsFixture.all)
    });

    it('facility can be removed from hub', function () {

    });
});
