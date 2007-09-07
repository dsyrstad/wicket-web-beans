package wicket.contrib.webbeans.model;

import java.io.Serializable;
import java.util.List;

/**
 * Meta data for a tab.
 */
public final class TabMetaData extends MetaData implements Serializable
{
    public static final String PARAM_LABEL = "label";
    
    private BeanMetaData beanMetaData;
    private String id;

    TabMetaData(BeanMetaData beanMetaData, String id, String label)
    {
        this.beanMetaData = beanMetaData;
        this.id = id;
        setLabel(label);
        
        consumeParameter(PARAM_LABEL);
    }

    /**
     * Applies parameters from a beanprops file to this tab.
     *
     * @param params
     */
    void applyBeanProps(List<Parameter> params)
    {
        for (Parameter param : params) {
            if (param.getName().equals(BeanMetaData.PARAM_PROPS)) {
                beanMetaData.applyProps(param.getValues(), id);
            }
            else {
                List<ParameterValue> values = param.getValues();
                if (values.size() != 1) {
                    throw new RuntimeException("Parameter " + param.getName() + "on tab " + id + " on bean " + beanMetaData.getBeanClass().getSimpleName() + " does not specify exactly one value.");
                }
                
                String value = values.get(0).getValue(beanMetaData.getComponent());
                setParameter(param.getName(), value);
            }
        }
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return getParameter(PARAM_LABEL);
    }

    public void setLabel(String label)
    {
        setParameter(PARAM_LABEL, label);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final TabMetaData other = (TabMetaData) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        }

        return id.equals(other.id);
    }

    @Override
    public int hashCode()
    {
        int result = 1;
        result = 31 * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}