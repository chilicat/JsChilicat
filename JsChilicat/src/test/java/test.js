(function() {
JsChilicat.newServer({
   init: function(context) {
       // make a complete directory available.
       // test could download files.
       this.attach("/html", context.baseDir);
       this.attachRestlet("/path-pattern-test/{command}", {
           doGet: function(request, response) {
               // get path variable and send it back to test case as JSON object.
               var command = request.getAttributes().command;
                // just send the object (object should not contain methods).
               response.sendJSON({ cmd: command});
           }
       });
       this.attachRestlet("/echo", {
           doGet: function(request, response) {
               this.doPost(request, response);
           },
           doPost: function(request, response) {
               // Get posted JSON and send it back to the test case.
               console.log(request.getJSON());
               response.sendJSON(request.getJSON());
           }
       });
       // Test scope of a restlet. Previously send members can be accessed on a next calls.
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
})();
