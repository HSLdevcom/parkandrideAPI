/**
 * Actively wait for an element to disappear either by becoming
 * invisible or by not being present. Wait up to specTimeoutMs
 * ignoring useless webdriver errors like StaleElementError.
 *
 * Usage:
 * Add `require('./waitAbsent.js');` in your onPrepare block or file.
 *
 * @example
 * expect($('.some-html-class').waitAbsent()).toBeTruthy();
 */
"use strict";

// Config
var specTimeoutMs = 10000; // 10 seconds

/**
 * Current workaround until https://github.com/angular/protractor/issues/1102
 * @type {Function}
 */
var ElementFinder = $('').constructor;

ElementFinder.prototype.waitAbsent = function() {
    var self = this;
    var driverWaitIterations = 0;
    var lastWebdriverError;
    function _throwError() {
        throw new Error("Expected '" + self.locator().toString() +
        "' to be absent or at least not visible. " +
        "After " + driverWaitIterations + " driverWaitIterations. " +
        "Last webdriver error: " + lastWebdriverError);
    };

    function _isPresentError(err) {
        lastWebdriverError = (err != null) ? err.toString() : err;
        // If there is an error trying to get the element the assume is gone
        return true;
    };

    return browser.driver.wait(function() {
        driverWaitIterations++;
        return self.isPresent().then(function(present) {
            if (present) {
                return self.isDisplayed().then(function(visible) {
                    lastWebdriverError = 'visible:' + visible;
                    return !visible;
                }, _isPresentError);
            } else {
                lastWebdriverError = 'present:' + present;
                return true;
            }
        }, _isPresentError);
    }, specTimeoutMs).then(function(waitResult) {
        if (!waitResult) { _throwError() };
        return waitResult;
    }, function(err) {
        _isPresentError(err);
        _throwError();
        return false;
    });
};