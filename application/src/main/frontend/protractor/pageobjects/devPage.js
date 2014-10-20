'use strict';

module.exports = function () {
    return {
        resetAll: function () {
            browser.get('/#/dev');
            element(by.id('wdDevResetAll')).click();
        }
    };
};
