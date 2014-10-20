'use strict';

var DevPage = (function(){
    function DevPage() {}

    DevPage.prototype.resetAll = function() {
        browser.get('/#/dev');
        element(by.id('wdDevResetAll')).click();
    };

    return DevPage;
})();

module.exports = DevPage;