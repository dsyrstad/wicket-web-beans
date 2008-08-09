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
    onFocus: function(field) {
        var element = document.getElementById("bfFocusField");
        if (element) {
            element.value = field.id;
        }
    },
     
    onChange: function(field) {
        alert("changed " + field.id);
    },

    refocus: function() {
        var element = document.getElementById("bfFocusField");
        if (element) {
            var field = element.value ? document.getElementById(element.value) : null;
            if (field) {
                field.focus();
            }
            else {
                bfFocusFirst(element.form, false);
            }
        }
    },
     
    focusFirst: function(form, focusFirstEmpty) {
        for (var i = 0; i < form.elements.length; i++ ) {
            if (form.elements[ i ].type != "hidden" &&
                !form.elements[ i ].disabled &&
                !form.elements[ i ].readOnly &&
                (!focusFirstEmpty || form.elements[i].value == "")) {
                var field = form.elements[ i ];
                field.focus();
                var element = document.getElementById("bfFocusField");
                if (element) {
                    element.value = field.id;
                }
                break;
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
