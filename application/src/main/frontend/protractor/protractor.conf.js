exports.config = {
    baseUrl:'http://localhost:8080',
    chromeDriver: '../node_modules/protractor/selenium/chromedriver',
    chromeOnly: false,
    rootElement: 'html',
    specs: [ 'specs/**/*.spec.js'],

//    capabilities: {
//      'browserName': 'phantomjs',
//      'phantomjs.binary.path':'../node_modules/phantomjs/lib/phantom/bin/phantomjs'
//    },
    capabilities: {
      'browserName': 'firefox'
    },

    jasmineNodeOpts: {
        onComplete: null,
        isVerbose: false,
        showColors: true,
        includeStackTrace: true,
        defaultTimeoutInterval: 30000
    },

    onPrepare: function(){
        require('protractor-linkuisref-locator')(protractor);

        require('jasmine-reporters');
        jasmine.getEnv().addReporter(new jasmine.JUnitXmlReporter(
            '.', true, true, 'protractor-results', true));
    }
};
