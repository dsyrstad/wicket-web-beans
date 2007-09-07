package wicket.contrib.webbeans.model;

import java.util.List;


/**
 * Parameter AST for BeanPropsParser. <p>
 * 
 * @author Dan Syrstad 
 */
public class Parameter
{
    private String name;
    private List<ParameterValue> values;

    Parameter(String name, List<ParameterValue> values)
    {
        this.name = name;
        this.values = values;
    }

    public String getName()
    {
        return name;
    }

    public List<ParameterValue> getValues()
    {
        return values;
    }

}
