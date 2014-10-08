exports.config = {
    baseUrl:'http://localhost:8080',
    chromeDriver: '../node_modules/protractor/selenium/chromedriver',
    chromeOnly: false,
    rootElement: 'html',
    specs: [ 'specs/**/*.spec.js'],

    capabilities: {
      'browserName': 'phantomjs',
      'phantomjs.binary.path':'../node_modules/karma-phantomjs-launcher/node_modules/phantomjs/lib/phantom/bin/phantomjs'
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
    }
};
