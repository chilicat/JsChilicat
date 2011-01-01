if(!JsChilicat.router) {
    throw "Router is not installed.";
}

var outerMethod = function() {
    return { name: "Name_____Soseas"}; 
};

// Test if outer method accessible.
JsChilicat.newServer({
    init: function() {
        this.attachRestlet("/outerMethod", {
            doGet: function(request, response) {
                response.sendJSON(outerMethod());
            }
        });
    }
});

JsChilicat.newServer({
    init: function(context) {
        console.log(context.baseDir);
        this.attach("/", context.baseDir);

        var handle = function(request, response) {
            var result = {
                path: request.getPath(),
                method: request.getMethod(),
                request: request.getJSON(),
                attributes: request.getAttributes(),
                header: request.getHeader()
            }
            response.sendJSON(result);
        };

        // A simple restlet that supports get and post.
        this.attachRestlet("/restlet", {
            doGet: function(request, response) {
                handle(request, response);
            },

            doPost: function(request, response) {
                // just delegate to toGet.
                this.doGet(request, response);
            }
        })

        // Test path pattern feature.
        this.attachRestlet("/restlet/{id}/{name}", {
            doGet: function(request, response) {
                handle(request, response);
            }
        });

        // what will happen if an error is thrown.
        this.attachRestlet("/error", {
            doGet: function(request, response) {
                throw "Error from java script";
            }
        });
    }
});