package net.sourceforge.wicketwebbeans.model;

import java.util.Date;
import java.util.List;

import net.sourceforge.wicketwebbeans.model.BeanFactory;
import net.sourceforge.wicketwebbeans.model.ParameterValueAST;

import org.apache.wicket.model.IModel;

public class TestBean
{
    private String stringProp;
    private int intProp;
    private double doubleProp;
    private float floatProp;
    private short shortProp;
    private boolean booleanProp;
    private long longProp;
    private Integer integerObjProp;
    private Double doubleObjProp;
    private Float floatObjProp;
    private Short shortObjProp;
    private Boolean booleanObjProp;
    private Long longObjProp;
    private String setterWithReturn;
    private List<ParameterValueAST> parameterValues;
    private IModel model;
    private IModel modelOfSubBean;
    private BeanFactory beanFactory;

    // TODO LATER: arrays, collections, maps

    public TestBean()
    {
    }

    public TestBean(String stringProp, int intProp, Integer integerObjProp)
    {
        this.stringProp = stringProp;
        this.intProp = intProp;
        this.integerObjProp = integerObjProp;
    }

    public String getStringProp()
    {
        return stringProp;
    }

    public void setStringProp(String stringProp)
    {
        assert beanFactory != null : "BeanFactory must be set first";
        this.stringProp = stringProp;
    }

    public int getIntProp()
    {
        return intProp;
    }

    public void setIntProp(int intProp)
    {
        this.intProp = intProp;
    }

    public double getDoubleProp()
    {
        return doubleProp;
    }

    public void setDoubleProp(double doubleProp)
    {
        this.doubleProp = doubleProp;
    }

    public boolean isBooleanProp()
    {
        return booleanProp;
    }

    public void setBooleanProp(boolean booleanProp)
    {
        this.booleanProp = booleanProp;
    }

    public float getFloatProp()
    {
        return floatProp;
    }

    public void setFloatProp(float floatProp)
    {
        this.floatProp = floatProp;
    }

    public short getShortProp()
    {
        return shortProp;
    }

    public void setShortProp(short shortProp)
    {
        this.shortProp = shortProp;
    }

    public long getLongProp()
    {
        return longProp;
    }

    public String getSetterWithReturnValue()
    {
        return this.setterWithReturn;
    }

    public TestBean setSetterWithReturnValue(String setterWithReturn)
    {
        this.setterWithReturn = setterWithReturn;
        return this;
    }

    public void setLongProp(long longProp)
    {
        this.longProp = longProp;
    }

    public Integer getIntegerObjProp()
    {
        return integerObjProp;
    }

    public void setIntegerObjProp(Integer integerObjProp)
    {
        this.integerObjProp = integerObjProp;
    }

    public Double getDoubleObjProp()
    {
        return doubleObjProp;
    }

    public void setDoubleObjProp(Double doubleObjProp)
    {
        this.doubleObjProp = doubleObjProp;
    }

    public Float getFloatObjProp()
    {
        return floatObjProp;
    }

    public void setFloatObjProp(Float floatObjProp)
    {
        this.floatObjProp = floatObjProp;
    }

    public Short getShortObjProp()
    {
        return shortObjProp;
    }

    public void setShortObjProp(Short shortObjProp)
    {
        this.shortObjProp = shortObjProp;
    }

    public Boolean getBooleanObjProp()
    {
        return booleanObjProp;
    }

    public void setBooleanObjProp(Boolean booleanObjProp)
    {
        this.booleanObjProp = booleanObjProp;
    }

    public Long getLongObjProp()
    {
        return longObjProp;
    }

    public void setLongObjProp(Long longObjProp)
    {
        this.longObjProp = longObjProp;
    }

    protected String getNotExposed()
    {
        return null;
    }

    protected void setNotExposed()
    {
    }

    public String getSetterNotExposed()
    {
        return null;
    }

    protected void setSetterNotExposed(String v)
    {
    }

    public Date getTypeNotSupported()
    {
        return null;
    }

    public void setTypeNotSupported(Date x)
    {
    }

    public void setThrowsException(String v)
    {
        throw new RuntimeException("Thrown from setter");
    }

    public List<ParameterValueAST> getParameterValues()
    {
        return parameterValues;
    }

    public void setParameterValues(List<ParameterValueAST> parameterValues)
    {
        this.parameterValues = parameterValues;
    }

    public IModel getModel()
    {
        return model;
    }

    public void setModel(IModel model)
    {
        this.model = model;
    }

    public IModel getModelOfSubBean()
    {
        return modelOfSubBean;
    }

    public void setModelOfSubBean(IModel modelOfSubBean)
    {
        this.modelOfSubBean = modelOfSubBean;
    }

    public BeanFactory getBeanFactory()
    {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }
}
