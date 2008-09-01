package net.sourceforge.wicketwebbeans.model.config;

import java.util.Date;

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

    // TODO arrays, collections, maps

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

}
