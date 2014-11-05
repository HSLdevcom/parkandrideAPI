'use strict';

var po = require('../../pageobjects/pageobjects.js');

describe('hub list', function () {
    var indexPage = po.indexPage({});
    var hubListPage = po.hubListPage({});

    it('is the default view', function () {
        indexPage.get();
        expect(hubListPage.isDisplayed()).toBe(true);
    });
});
