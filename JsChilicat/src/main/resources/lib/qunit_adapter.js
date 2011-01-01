if(!JsChilicat) {
    throw "JsChilicat isn't loaded.";
}

    if(!window.console) {
        window.console = {
            log: function(level) {},
            debug: function() {},
            info: function() {},
            warn: function() {},
            error: function(){}
        };
    }

var initTestReport = function() {
    (function(that) {
        var testFn = that.test;
        var moduleFn = that.module;

        that.test = function(name, expected, callback, async) {
            JsChilicat.bus.testAdded(name);
            testFn.apply(this, arguments);
        },

        that.module = function(name, testEnvironment) {
            JsChilicat.bus.moduleAdded(name);
            moduleFn.apply(this, arguments);
        },

        that.scriptScope = function(name) {

        }
    })(this);

    // plugin into qunit
    QUnit.moduleStart = function(name, testEnvironment) {
        JsChilicat.bus.moduleStart(name);
    };

    QUnit.moduleDone = function(name, failures, total) {
        if (total > 0) {
            JsChilicat.bus.moduleDone(name);
        }
    };

    QUnit.testStart = function(name) {
        JsChilicat.bus.testStarted(name);
    };

    QUnit.testDone = function(name, failures, total) {
        if (failures > 0) {
            JsChilicat.bus.testFailed(name, "");
        } else {
            JsChilicat.bus.testPassed(name);
        }
    };

    QUnit.log = function(result, message) {
        if (!result) {
            JsChilicat.bus.log((result ? 'PASS  ' : 'FAIL  ') + message);
        }
    };

    QUnit.done = function(fail, pass) {
        if(JsChilicat.bus.done) {
            JsChilicat.bus.done();
        }
    };
};

initTestReport();