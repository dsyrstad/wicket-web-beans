package wicket.contrib.webbeans.util;

import wicket.Component;
import wicket.Localizer;


/**
 * Static wicket utilities.
 * 
 * @author Dan Syrstad
 */
public class WicketUtil
{
    private WicketUtil() { }
    
    /**
     * Substitutes macros of the form "${key}" in string to the corresponding value of
     * "key" in localizer. 
     *
     * @param string the input string containing the macros.
     * @param component the component whose Localizer is used to localize the string.
     * 
     * @return a String with the macros expanded. If a macro key cannot be found in 
     *  localizer, the macro is substituted with "?string?".
     */
    public static String substituteMacros(String string, Component component) 
    {
        Localizer localizer = component.getLocalizer();
        StringBuilder buf = new StringBuilder(string);
        int idx = 0;
        while ((idx = buf.indexOf("${", idx)) >= 0) {
            int endIdx = buf.indexOf("}", idx + 2);
            if (endIdx > 0) {
                String key = buf.substring(idx + 2, endIdx);
                String value = localizer.getString(key, component, '?' + key + '?');
                if (value != null) {
                    buf.replace(idx, endIdx + 1, value);
                }
                else {
                    // Nothing found, skip macro.
                    idx += 2;
                }
            }
        }
        
        return buf.toString();
    }
}
