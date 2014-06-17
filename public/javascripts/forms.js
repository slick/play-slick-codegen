function FormError(message) {
   this.message = message;
   this.name = "FormError";
}

function cast(type, value){
    switch(type){
        case "String":
            if (typeof value != "string") throw new FormError("You must submit a text value");
            break;
        case "Number":
            if (isNaN(parseFloat(value))) throw new FormError("You must submit a number value");
            break;
        case "java.sql.Date":
            if (new Date(value) == "Invalid Date") throw new FormError("You must submit a valid date format");
            break;
    }
}

function hasError(kind, rule, value){
    switch (kind){
        case "required":
            if (!!rule && !value) throw new FormError("This field is required");
        case "type":
            !!value && cast(rule, value)
    }
}

function validate(schema, callback){
    $(function() {
        $.each(schema, function(key, rules) {
            var dom = $("#"+key),
                startVal = dom.val();
            dom.on('blur', function() {
                var value = dom.val();
                try {
                    $.each(rules, function(kind, rule) {
                        hasError(kind, rule, value);
                    });
                } catch(e) {
                    callback(dom, value, e)
                    return;
                }
                callback(dom, value);
            });
        });
    });
}
