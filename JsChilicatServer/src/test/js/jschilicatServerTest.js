module("Server API");

var superTest = test;
test = function(name, callback) {
    fn = function() {
        try {
            callback.apply(this, arguments);
        } finally {
            JsChilicat.disposeServers();
        }
    };

    superTest.apply(this, [name, fn]);
}

test("Interface", function() {
    expect(4);
    ok(JsChilicat, "Namespace 'JsChilicat' is not defined.");
    ok(JsChilicat.newServer, "Method 'JsChilicat.newServer' is not defined.");
    ok(JsChilicat.disposeServers, "Method 'JsChilicat.disposeServers' is not defined.");
    ok(JsChilicat.startServer, "Method 'JsChilicat.startServer' is not defined.");
});

test("Interface Server.init", function() {
    expect(2);
    JsChilicat.newServer({
        init: function(context) {
            ok(this.attach, "Server must support this.attach(...)");
            ok(this.attachRestlet, "Server must support this.Restlet(...)");
        }
    });
    JsChilicat.startServer({});
  //  JsChilicat.disposeServers();
});

test("JsChilicat.start will not fail without Servers.", function() {
    expect(0);
    JsChilicat.startServer({});
});

test("Server Instance", function() {
    expect(3);

    JsChilicat.newServer({
        init: function(context) {
            ok(!this.called, "init is called twice");
            ok(context, "Context parameter is not defined [JsChilicat.server.init()].");
            equals("value", context.someValue, "Context.someValue is not defined.");
            this.called = true;
        }
    });

    JsChilicat.startServer({ someValue: "value" });

    JsChilicat.disposeServers();

    JsChilicat.startServer({ someValue: "second call - should not work" });

});

test("Server.startServer Exception", function() {
    expect(6);

    JsChilicat.newServer({
        init: function(context) {
            ok(false, "Should no be called");
        }
    });

    try {
        JsChilicat.startServer(null);
    } catch(e) {
        ok(e.type, "Exception must have type attribute");
        equals("undefined", e.type, "Type is incorrect");
    }

    try {
        JsChilicat.startServer();
    } catch(e) {
        ok(e.type, "Exception must have type attribute");
        equals("undefined", e.type, "Type is incorrect");
    }

    try {
        JsChilicat.startServer(undefined);
    } catch(e) {
        ok(e.type, "Exception must have type attribute");
        equals("undefined", e.type, "Type is incorrect");
    }
    
    JsChilicat.disposeServers();

});


test("Mutliple Server Instance", function() {
    expect(6);

    var impl = {
        init: function(context) {
            ok(!this.called, "init is called twice");
            ok(context, "Context parameter is not defined [JsChilicat.server.init()].");
            equals("value", context.someValue, "Context.someValue is not defined.");
            this.called = true;
        }
    }

    // register multiple instances.
    JsChilicat.newServer(impl);
    JsChilicat.newServer(impl);

    JsChilicat.startServer({ someValue: "value" });

    JsChilicat.disposeServers();
});


test("Server.init.attach", function() {
    expect(5);

    JsChilicat.newServer({
        init: function(context) {
            ok(true, "Init must be called.");
            this.attach("/index.html", "../other.html");
            this.attach("/resourses", "../resourceFolder");

            try {
                this.attach();
            } catch(e) {
                ok(e.type, "Exception must have type attribute");
                equals("undefined", e.type, "Type is incorrect");
            }

            try {
                this.attach("string");
            } catch(e) {
                ok(e.type, "Exception must have type attribute");
                equals("undefined", e.type, "Type is incorrect");
            }
        }
    });

    JsChilicat.startServer({ someValue: "value" });
    JsChilicat.disposeServers();
});