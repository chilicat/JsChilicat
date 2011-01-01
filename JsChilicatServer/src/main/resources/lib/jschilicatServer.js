var JsChilicat = JsChilicat || {};

/**
 * Init native router.
 **/
(function(JsChilicat) {
    try {
        JsChilicat.router = new NativeRouter();
    } catch(e) {
        // ok ignore. For test reasons.    
    }
})(JsChilicat);


/**
 * Helper functions
 */
(function(JsChilicat) {
    JsChilicat.extend = function(dest, impl) {
        for(var idx in impl) {
            if (impl.hasOwnProperty(idx) ) {
                dest[idx] = impl[idx];
            }
        }
    };
})(JsChilicat);

/**
 * Server
 */
(function(JsChilicat) {
    var Server = function(impl) {
        this.impl = impl;
    };

    var exceptions = {
        /**
         * New exception. A chilicat exception always needs a type and a message.
         */
        exception: function(type, message) {
            return {
                type: type,
                message: message
            }
        },

        /**
         * Check if obj undefined or null.
         */
        undefined: function(obj) {
            return (obj === undefined || obj === null);
        },

        /**
         * New undefined exception.
         */
        undefinedEx: function(name) {
            return this.exception("undefined", "Undefined parameter '" + name + "'");
        }
    }

    JsChilicat.extend(Server.prototype, exceptions);
    JsChilicat.extend(Server.prototype, {
        config: {
            resoures: [],
            restlets: []
        },

        /**
         * Will initialize the server.
         * The context should contain information about
         * the test base dir, etc..
         */
        start : function(context) {
            if(this.impl && this.impl.init) {
                this.impl.init.apply(this, [context]);
            }
        },

        attachRestlet: function(alias, restlet) {
             if(this.undefined(alias)) {
                throw this.undefinedEx("alias");
            }

            if(this.undefined(restlet)) {
                throw this.undefinedEx("restlet");
            }

            if(this.undefined(restlet.doGet) && this.undefined(restlet.doPost)) {
                throw this.undefinedEx("A restlet must define at least on of following methods: doGet, doPost" );
            }

            this.config.restlets.push({
                alias: alias,
                restlet: restlet
            });

            if(JsChilicat.router) {
                JsChilicat.router.attachRestlet(alias, restlet);
            }
        },

        /**
         * Will attach a file resource on defined alias on the server.
         */
        attach: function(alias, resource) {
            if(this.undefined(alias)) {
                throw this.undefinedEx("alias");
            }

            if(this.undefined(resource)) {
                throw this.undefinedEx("resource");
            }

            this.config.resoures.push({
                alias: alias,
                resource: resource
            });

            if(JsChilicat.router) {
                JsChilicat.router.attach(alias, resource);
            }
       }
   });

   JsChilicat.extend(JsChilicat, exceptions);

   /**
    * Add server methods to JsChilicat.
    **/
   JsChilicat.extend(JsChilicat, {
        serverList: [], // list of registered server instances.

        /**
         * Creates a new server instance. It will usally only called once.
         */
        newServer: function(init) {
            this.serverList.push(new Server(init));
        },

        /**
         * Will reset servers. Will usally not be called API users.
         * Method is clearly only needed by tests.
         */
        disposeServers: function() {
           delete JsChilicat.serverList;
           this.serverList = [];
        },

        /**
         * Will start (init) all registered server instances.
        **/
        startServer: function(context) {
            if(this.undefined(context)) {
                throw this.undefinedEx("context");
            }
            if(this.serverList) {
                for(var idx=0; idx<this.serverList.length; idx++) {
                    //this.serverList[idx].start(context);
                    this.serverList[idx].start(context);
                }
            }
       }
    });

})(JsChilicat);


/**
 * Request and Response
 */
(function(JsChilicat) {
    Request = function(impl) {
        this.impl = impl;
    };

    JsChilicat.extend(Request, {
        getPath: function() {
            return this.impl.getPath();
        }
    });

    JsChilicat.extend(JsChilicat, {
        createRequest: function(impl) {
            return new Request(impl);
        }
    });
})(JsChilicat);