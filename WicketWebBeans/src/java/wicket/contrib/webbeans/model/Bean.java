package wicket.contrib.webbeans.model;

import java.util.List;


/**
 * Bean AST for BeanPropsParser. <p>
 * 
 * @author Dan Syrstad 
 */
public class Bean
{
    private String name;
    private String context;
    private String extendsContext;
    private List<Parameter> parameters;

    public Bean(String name, String context, String extendsContext, List<Parameter> parameters)
    {
        this.name = name;
        this.context = context;
        this.extendsContext = extendsContext;
        this.parameters = parameters;
    }

    public String getName()
    {
        return name;
    }

    public String getContext()
    {
        return context;
    }

    public String getExtendsContext()
    {
        return extendsContext;
    }

    public List<Parameter> getParameters()
    {
        return parameters;
    }

}
