
// Test if outer method accessible.
JsChilicat.newServer({
    init: function(context) {
        // content base == working directory.
        // Following line will give test cases
        // access to all files in the working directory
        this.attach("/", context.baseDir);
    }
});