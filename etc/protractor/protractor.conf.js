exports.config = {
    baseUrl: process.env.SERVER_URL,
    chromeDriver: 'node_modules/protractor/selenium/chromedriver',
    chromeOnly: false,
    rootElement: 'html',
    specs: [ 'specs/**/*.spec.js'],

    capabilities: {
      'browserName': 'firefox'
    },

    jasmineNodeOpts: {
        onComplete: null,
        isVerbose: true,
        showColors: false,
        includeStackTrace: true,
        defaultTimeoutInterval: 60000,
        showTiming: true
    },

    onPrepare: function(){
        require('protractor-linkuisref-locator')(protractor);

        require('jasmine-reporters');
        jasmine.getEnv().addReporter(new jasmine.JUnitXmlReporter(
            '.', true, true, 'protractor-results', true));

        browser.driver.manage().window().setSize(1280, 1024);
    }
};
