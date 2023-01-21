package lt.svako.svieziasoras;

import java.io.Serializable;
import java.util.Date;

public class PostPlaces implements Serializable
{
    private static final String placeSharedPreferencesString = "placeInfo";
    private static final String listPlaceSharedPreferencesString = "placesInfoList";
    private static final String listExpiryDatePlaceSharedPreferencesString = "placesInfoListExpiryDate";

    private String code;
    private String name;
    private String administrativeDivision;
    private String countryCode;

    public PostPlaces(String code, String name, String administrativeDivision, String countryCode)
    {
        this.code = code;
        this.name = name;
        this.administrativeDivision = administrativeDivision;
        this.countryCode = countryCode;
    }

    public static String getListExpiryDatePlaceSharedPreferencesString() {
        return listExpiryDatePlaceSharedPreferencesString;
    }

    public static String getPlaceSharedPreferencesString()
    {
        return placeSharedPreferencesString;
    }
    public static String getListPlaceSharedPreferencesString()
    {
        return listPlaceSharedPreferencesString;
    }


    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAdministrativeDivision()
    {
        return administrativeDivision;
    }

    public void setAdministrativeDivision(String administrativeDivision)
    {
        this.administrativeDivision = administrativeDivision;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }
}
