var first = true;
var hello;
if(first) { hello = "A" } else { hello = "B" }


Cat = function(pawCount, tailLength, color) {
    this.pawCount = pawCount; 
    this.tailLength = tailLength;
    this.color = color;
};

Cat.prototype =  {
    getTailLength : function() {
        return this.tailLength;
    },

    getPawCount: function() {
        return this.pawCount;
    },

    setIfColor : function(color, value) {
        if(this.color == color) {
            this.value = value;
            return true;
        }
        return false;
    }
};