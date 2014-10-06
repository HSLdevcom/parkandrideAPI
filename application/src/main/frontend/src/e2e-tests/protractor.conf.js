exports.config = {
    baseUrl:'http://localhost:9100',
    capabilities: {
      'browserName': 'phantomjs'
    },

    specs: [
        'e2e-tests/**/*.js'
    ],

    jasmineNodeOpts: {
        showColors: true,
        defaultTimeoutInterval: 30000
    }
};
