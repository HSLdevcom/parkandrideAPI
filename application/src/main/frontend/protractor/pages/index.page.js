'use strict';

var IndexPage = (function(){
    function IndexPage() {}

    IndexPage.prototype.get = function() {
      browser.get('/');
    };

    return IndexPage;
})();

module.exports = IndexPage;