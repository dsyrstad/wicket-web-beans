package wicket.contrib.webbeans.model;

import wicket.contrib.webbeans.model.NonJavaEnum;

import java.io.Serializable;
import java.util.List;

/**
 * Base implementation of NonJavaEnum to make it look similar to a Java Enum.
 * 
 * @author Marc Stock
 * @author Dan Syrstad
 */
public abstract class BaseNonJavaEnum implements NonJavaEnum, Serializable
{
    protected String name;
    protected String displayValue;

    public BaseNonJavaEnum(String name, String displayValue)
    {
        this.name = name;
        this.displayValue = displayValue;
    }

    public String name()
    {
        return name;
    }

    public String getDisplayValue()
    {
        return displayValue;
    }

    public void setDisplayValue(String displayValue)
    {
        this.displayValue = displayValue;
    }

    /**
     * Get the Enum for the given name.
     *
     * @param enumValue name to match
     *
     * @return a Enum, or null if not found.
     * @param enums cachedEnums to search through
     */
    public static BaseNonJavaEnum valueOf(String enumValue, List<? extends BaseNonJavaEnum> enums)
    {
        if (enumValue == null)
            return null;

        for (BaseNonJavaEnum nonJavaEnum : enums) {
            if (nonJavaEnum.name().equals(enumValue)) {
                return nonJavaEnum;
            }
        }

        return null;
    }

    @Override
    public String toString()
    {
        return getDisplayValue();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BaseNonJavaEnum)) {
            return false;
        }

        BaseNonJavaEnum other = (BaseNonJavaEnum)obj;
        return name().equals(other.name());
    }
}
