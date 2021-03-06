// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

exports.config = {
    // baseUrl: process.env.SERVER_URL,
    // chromeDriver: 'node_modules/protractor/selenium/chromedriver',
    // directConnect: true,
    rootElement: 'html',
    specs: ['specs/**/*.spec.js'],

    seleniumAddress: 'http://localhost:4444/wd/hub',
    framework: 'jasmine2',
    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            args: ['no-sandbox']
        }
    },

    jasmineNodeOpts: {
        onComplete: null,
        isVerbose: true,
        showColors: false,
        includeStackTrace: true,
        defaultTimeoutInterval: 60000,
        showTiming: true
    },

    onPrepare: function () {
        require('protractor-linkuisref-locator')(protractor);

        require('jasmine-reporters');
        jasmine.getEnv().addReporter(new jasmine.JUnitXmlReporter(
            '.', true, true, 'protractor-results', true));

        var ScreenshotReporter = require('protractor-html-screenshot-reporter');
        jasmine.getEnv().addReporter(new ScreenshotReporter({
            baseDirectory: 'screenshots'
        }));

        browser.driver.manage().window().setSize(1280, 1024);

        global.beforeAll = function (fn) {
            var firstTime = true;
            beforeEach(function () {
                if (firstTime) {
                    fn();
                }
                firstTime = false;
            });
        };

        require('./specs/waitAbsent');
    }
};
