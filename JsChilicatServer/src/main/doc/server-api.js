
attachRestlet('/dummy', {
    doGet: function() {
        print('Hello Restlet');
	}
});


// Contains enviroment information which can be utilized to set up the resltets.
ContextInfo {
	baseDir: "test-base-path", <resource>
	testFile: "test-file-path", <resource>
}

Resource {
	getName();
	getPath();
}

var test = function() {
    return "a";
}

JsChilicat.testSet(function(context) {

    var dir = context.baseDir(); // directory of this file.

    context.src("src/net/foo/a.js", "src/b.js", "src/c.js");
    context.src("src/net/folderA", "src/b.js", "src/net/folderB");

    context.test("test/aTest.js", "test/bTest.js");
    context.test("test/net/folderA", "test/bTest.js", "test/net/folderB");

    context.server({
        init: function() {
            attach(".", ".");
            attachRestlet({
                doGet: function() {
                    resp.send(Hello);
                }
            });
        }
    };
});

JsChilicat.server({
	init: function(contextInfo) {
		this.attach("/index.html", "../resources/default.html");

		this.attachDir("/", ".");

		attachRestlet("/service/{test}/api", {
			doGet(Response resp, Request request) {
				if(request.getJson().enbaled) {
					resp.sendJson({
						"message": "enabled",
						"total": "12"
					});
				} else {
					resp.sendJson({
						"message": "not enabled",
						"total": "10"
					});
				}
			},

			doPost(Response resp, , Request request) {

			}

			doDelete(Response resp, , Request request) {
				resp.send("../resource/fail.json");
			}
		})
	}
});