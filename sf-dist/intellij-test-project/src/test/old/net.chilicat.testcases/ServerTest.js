module("Basics");

test("Path Pattern Test", function() {
    expect(2);
    $.getJSON("path-pattern-test/firstCommand", function(data) {
        equals(data.cmd, "firstCommand");
    });

    $.getJSON("path-pattern-test/secondCommand", function(data) {
        equals(data.cmd, "secondCommand");
    });
});

test("Post Echo Test", function() {
    expect(4);

    var postData = { name: "Daniel", time: "now" }

    $.post("echo", postData, function(data) {
        //data  = $.parseJSON(data);
        equals(data.name, "Daniel");
        equals(data.time, "now");
    }, "json");

    $.post("echo", postData, function(data) {
        data = $.parseJSON(data);
        equals(data.name, "Daniel");
        equals(data.time, "now");
    });
});

module("Session Data");

test("Counter Test", function() {
    expect(2);
    $.getJSON("counter", function(data) {
        equals(data[0], 1);
        $.getJSON("counter", function(data) {
            equals(data[0], 2);
        });
    });
});

module("Include Extenral HTML");

test("Load include fragment", function() {
    expect(2);
    var body = $("body");
    ok(body);
    
    body.load("html/include.html", function() {
        //equals($("#helloWorld").html(), "Hello World Text");
        equals($("#someData").html(), "Some data"); // <p id="someData">Some data</p>
    });
});

