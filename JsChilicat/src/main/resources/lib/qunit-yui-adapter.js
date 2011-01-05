var QUnit = {
    modules: [],
    init: function() {
        YUITest.TestRunner.clear();
    },
    reset: function() {
        YUITest.TestRunner.clear();
    },

    start: function() {
        var suite = new YUITest.TestSuite(window.location || "QUnit Test suit");
        for(var i=0, l=QUnit.modules.length; i<l; i++) {
            suite.add(new YUITest.TestCase(QUnit.modules[i]));
        }
        //add it to be run
        YUITest.TestRunner.add(suite);
        YUITest.TestRunner.run();
    }
};

module = function(name) {
    QUnit.modules.push({
        name: name
    });
};

ok = function(value, message) {
    YUITest.Assert.isTrue(!!value, message);
};

equal = function(actual, expected, message) {
    YUITest.Assert.areEqual(expected, actual, message);
};

notEqual = function(actual, expected, message) {
    YUITest.Assert.areNotEqual(expected, actual, message);
};

strictEqual = function( actual, expected, message ) {
    YUITest.Assert.areSame(expected, actual, message);
};

notStrictEqual = function( actual, expected, message ) {
    YUITest.Assert.areNotSame(expected, actual, message);
};

raises = function(state, message) {
        YUITest.Assert._increment();
        var error = false;
        try {
            state();
        } catch (thrown) {
            // expected
            return;
        }

        //if it reaches here, the error wasn't thrown, which is a bad thing
        throw new YUITest.AssertionError(YUITest.Assert._formatMessage(message, "Error should have been thrown."));
};

deepEqual = function(actual, expected, message ) {
    YUITest.ArrayAssert.itemsAreEqual(expected, actual, message);
};

notDeepEqual = function(actual, expected, message ) {
    YUITest.Assert._increment();
    //first check array length
    if (expected.length !== actual.length){
        return;
    } else {
        //begin checking values
        for (var i=0; i < expected.length; i++){
            if (expected[i] != actual[i]){
                return;
            }
        }
    }

    throw new YUITest.Assert.ComparisonFailure(YUITest.Assert._formatMessage(message, "Arrays are equal."));
};


asyncTest = function(name, expected, fn) {
    if(arguments.length === 2) {
        fn = expected;
        expected = 1;
    }

};

test = function(name, fn) {
    var wrapper = function() {
        var that = this;
        window.expect = function(num) {
            fn.expect = num;
        }

        window.start = function(fn) {
            fn = fn || function() {};
            that.resume(fn);
        }

        window.stop = function(timeout) {
            that.wait(timeout)
        }

        fn();

        if(fn.expect) {
            // -1 because this check should not be counted.
            YUITest.Assert.areEqual(fn.expect, Assert._getCount()-1, "Expected Assertions");
        } else {
            // workaround to meet QUnit requirements:
            YUITest.Assert.isTrue(true);
        }

        delete window.expect
        delete window.start
        delete window.stop
    }

    QUnit.modules[QUnit.modules.length-1][name] = fn;
};


/*
autorun = false;
if ( typeof document === "undefined" || document.readyState === "complete" ) {
	autorun = true;
}*/

addEvent(window, "load", function() {
   // if(autorun) {
        QUnit.start();
    //}
});

function addEvent(elem, type, fn) {
	if ( elem.addEventListener ) {
		elem.addEventListener( type, fn, false );
	} else if ( elem.attachEvent ) {
		elem.attachEvent( "on" + type, fn );
	} else {
		fn();
	}
};