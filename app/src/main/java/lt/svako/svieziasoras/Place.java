package lt.svako.svieziasoras;

public class Place
{
    private String code;
    private String name;
    private String administrativeDivision;
    private String country;
    private String countryCode;
    private Coordinates coordinates;

    public Place(String code, String name, String administrativeDivision, String country, String countryCode, Coordinates coordinates)
    {
        this.code = code;
        this.name = name;
        this.administrativeDivision = administrativeDivision;
        this.country = country;
        this.countryCode = countryCode;
        this.coordinates = coordinates;
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

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }
}
