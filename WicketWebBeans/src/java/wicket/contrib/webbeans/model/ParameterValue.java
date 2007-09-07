package wicket.contrib.webbeans.model;

import java.util.ArrayList;
import java.util.List;

import wicket.Component;
import wicket.contrib.webbeans.util.WicketUtil;


/**
 * ParameterValue AST for BeanPropsParser. <p>
 * 
 * @author Dan Syrstad 
 */
public class ParameterValue
{
    private String value;
    private List<Parameter> parameters = new ArrayList<Parameter>();

    public ParameterValue(String value)
    {
        this.value = value;
    }

    public List<Parameter> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * @return the raw value without substitution of macros.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @return the raw value with substitution of macros based on the component's Localizer.  
     */
    public String getValue(Component component)
    {
        return WicketUtil.substituteMacros(value, component);
    }
}
