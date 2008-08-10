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
    focusFieldId: null,
    
    onFocus: function(field) {
        wwbBeanForm.focusFieldId = field.name;
    },
     
    onChange: function(field) {
        var form = document.forms[field.form.ajaxFormId.value];
        form.elements['focusFieldId'].value = wwbBeanForm.focusFieldId;
        form.elements['submitFieldName'].value = field.name;
        form.elements['submitFieldValue'].value = field.value;
        eval(form.attributes['onajax'].value);
    },

    refocus: function() {
        var field = document.getElementById(wwbBeanForm.focusFieldId);
        if (field) {
            field.focus();
        }
        else {
            wwbBeanForm.focusFirst(false);
        }
    },
     
    focusFirst: function(focusFirstEmpty) {
        for (var form in document.forms) {
            for (var i = 0; i < form.elements.length; i++ ) {
                if (form.elements[ i ].type != "hidden" &&
                    !form.elements[ i ].disabled &&
                    !form.elements[ i ].readOnly &&
                    (!focusFirstEmpty || form.elements[i].value == "")) {
                    var field = form.elements[ i ];
                    field.focus();
                    wwbBeanForm.focusFieldId = field.name;
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
