// Test if outer method accessible.
JsChilicat.newServer({
    init: function(context) {
        this.attach("/html", context.baseDir);
        this.attachRestlet("/path-pattern-test/{command}", {
            doGet: function(request, response) {
                var command = request.getAttributes().command;
                response.sendJSON({ cmd: command});
            }
        });
        
        this.attachRestlet("/echo", {
            doGet: function(request, response) {
                this.doPost(request, response);
            },

            doPost: function(request, response) {
                response.sendJSON(request.getJSON());
            }
        });


        this.attachRestlet("/counter", {
            counter: 0,
            doGet: function(request, response) {
                ++this.counter;
                response.sendJSON(
                    this.counter
                );
            },
        });
    }
});