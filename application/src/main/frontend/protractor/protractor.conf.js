exports.config = {
    seleniumAddress: 'http://localhost:4444/wd/hub',
    baseUrl:'http://localhost:9100',
    rootElement: 'html',
    specs: [ './**/*.spec.js'],
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
    }
};
