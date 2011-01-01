module("TestCase with Html fragments");

test("Elements exist", function() {
    expect(4);

    ok($("#content"));
    ok($("#root"));
    
    equals($("#content").length, 1);

    $("#content").each(function() {
        equals(this.html,"Hello Html");
    });
});