package wicket.contrib.webbeans.examples.customfields;

import java.util.ArrayList;
import java.util.List;

import wicket.contrib.webbeans.model.BaseNonJavaEnum;

public class Country extends BaseNonJavaEnum
{
    private volatile static List<Country> cachedEnums;

    public Country(String name, String displayValue)
    {
        super(name, displayValue);
    }

    /**
     * Get the enumerated values.
     *
     * @return the list of values. An empty list is returned if nothing is found.
     */
    public static List<Country> values()
    {
        if (cachedEnums == null) {
            // This is where you would load a list of countries from a database. 
            cachedEnums = new ArrayList<Country>();
            cachedEnums.add( new Country("USA", "United States") );
            cachedEnums.add( new Country("CAN", "Canada") );
            cachedEnums.add( new Country("MEX", "Mexico") );
            cachedEnums.add( new Country("GBR", "Great Britian") );
            cachedEnums.add( new Country("RUS", "Russia") );
        }

        return cachedEnums;
    }

    /**
     * Get the country enum value for the given name.
     *
     * @param enumValue name to match
     *
     * @return a Country, or null if not found.
     */
    public static Country valueOf(String enumValue)
    {
        return (Country)BaseNonJavaEnum.valueOf(enumValue, values());
    }

}