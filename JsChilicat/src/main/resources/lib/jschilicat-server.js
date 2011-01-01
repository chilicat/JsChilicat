JsChilicat.newServer({
    init: function(context) {
       this.attachRestlet("/jschilicat", {
           notify: function(json) {

               if(!JsChilicat.bus[json.type]) {
                    throw "Undefined type: " + json.type;
               }

                var params = json.params;

                switch(json.type) {
                    case "testAdded":
                        JsChilicat.bus.testAdded(params[0]);
                        break;
                    case "testStarted":
                        JsChilicat.bus.testStarted(params[0]);
                        break;
                    case "testPassed":
                        JsChilicat.bus.testPassed(params[0]);
                        break;
                    case "testFailed":
                        JsChilicat.bus.testFailed(params[0], params[1]);
                        break;
                    case "moduleAdded":
                        JsChilicat.bus.moduleAdded(params[0]);
                        break;
                    case "moduleStart":
                        JsChilicat.bus.moduleStart(params[0]);
                        break;
                    case "moduleDone":
                        JsChilicat.bus.moduleDone(params[0]);
                        break;
                    case "log":
                        // Currently I don't support dynamic arguments
                        for(var i=0; i<params.length; i++) {
                            JsChilicat.bus.log(params[i]);
                        }
                        break;

                }
           },
           
           doPost: function(request, response) {
                var json = request.getJSON();
                var data = JSON.parse(json.__text__).data;
                for(var i=0; i<data.length;i++) {
                    this.notify(data[i]);
                }
           }
       });
    }
});