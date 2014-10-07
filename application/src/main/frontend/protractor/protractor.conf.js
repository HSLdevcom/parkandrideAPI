exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    baseUrl:'http://localhost:8080',
    rootElement: 'html',
    specs: [ 'specs/**/*.spec.js'],
    allScriptsTimeout: 10000,
    chromeOnly: false,

    capabilities: {
      'browserName': 'phantomjs',
      'phantomjs.binary.path':'node_modules/karma-phantomjs-launcher/node_modules/phantomjs/lib/phantom/bin/phantomjs'
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
