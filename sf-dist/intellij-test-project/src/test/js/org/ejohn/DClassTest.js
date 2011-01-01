module("DClassTest");

test("Interface", function() {
	ok(Class);
	ok(Class.extend);
});

test("Extend", function() {
	console.log("Test Extned");
	expect(8);
	
	console.log("Create new Person");
	var p = new Person(true);
	ok(p.dance()); // => true

	console.log("Create new Ninja");
	var n = new Ninja();
	ok(!n.dance()); // => false
	ok(n.swingSword()); // => true

	console.log("Check instance of...");
	// Should all be true
	ok(p instanceof Person);
	ok(p instanceof Class);
	ok(n instanceof Ninja);
	ok(n instanceof Person);
	ok(n instanceof Class);
});