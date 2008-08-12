var wwbBeanForm =  {
    indicatorOn: function() {
        var element = document.getElementById("beanFormIndicatorOn");
        if (element) element.style.display = "block";
        element = document.getElementById("beanFormIndicatorError");
        if (element) element.style.display = "none";
        element = document.getElementById("beanFormIndicatorOff");
        if (element) element.style.display = "none";
    },
    
    indicatorOff: function() {
        var element = document.getElementById("beanFormIndicatorOff");
        if (element) element.style.display = "block";
        element = document.getElementById("beanFormIndicatorOn");
        if (element) element.style.display = "none";
        element = document.getElementById("beanFormIndicatorError");
        if (element) element.style.display = "none";
        wwbBeanForm.refocus();
    },
     
    indicatorError: function() {
        element = document.getElementById("beanFormIndicatorOn");
        if (element) element.style.display = "none";
        element = document.getElementById("beanFormIndicatorOff");
        if (element) element.style.display = "block";
    },

    // Track field with last focus in case we're refreshed.
    focusFieldName: null,
    
    onFocus: function(field) {
        wwbBeanForm.focusFieldName = field.name;
    },
     
    onChange: function(field) {
        var form = field.form;
        form.elements['focusFieldName'].value = wwbBeanForm.focusFieldName;
        form.elements['submitFieldName'].value = field.name;
        eval(form.attributes['onajax'].value);
    },

    refocus: function() {
        var fields = document.getElementsByName(wwbBeanForm.focusFieldName);
        if (fields && fields.length > 0) {
            var field = fields[0];
        }
        
        if (field) {
            field.focus();
        }
        else {
            wwbBeanForm.focusFirst(false);
        }
    },
     
    focusFirst: function(focusFirstEmpty) {
        for (var formId in document.forms) {
            var form = document.forms[formId];
            for (var i = 0; form.elements && i < form.elements.length; i++ ) {
                if (form.elements[ i ].type != "hidden" &&
                    !form.elements[ i ].disabled &&
                    !form.elements[ i ].readOnly &&
                    (!focusFirstEmpty || form.elements[i].value == "")) {
                    var field = form.elements[ i ];
                    field.focus();
                    wwbBeanForm.focusFieldName = field.name;
                    break;
                }
            }
        }
    },
     
    handleGlobalKeyEvent: function(event) {
        var keyCode;
        if (event.which) {
            keyCode = event.which; // Mozilla
        }
        else {
            keyCode = event.keyCode; // IE
        }
     
        if (keyCode == 13) {
            var element = document.getElementById("bfDefaultButton");
            if (element) {
                element.onclick();
                return false;
            }
        }
        
        return true;
    }
};
