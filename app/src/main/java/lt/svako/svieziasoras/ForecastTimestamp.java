package lt.svako.svieziasoras;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ForecastTimestamp implements Serializable
{
    private String forecastTimeUtc;
    private float airTemperature;
    private float feelsLikeTemperature;
    private int windSpeed;
    private int windGust;
    private int windDirection;
    private int cloudCover;
    private int seaLevelPressure;
    private int relativeHumidity;
    private float totalPrecipitation;
    private String conditionCode;

    public enum ConditionCodeTypes
    {
        //ateiti keisis resources reiksmes ir visos bus orginalios
        clear("clear", "giedra", R.drawable.ic_sunny),
        partlyCloudy("partly-cloudy", "mažai debesuota", R.drawable.ic_baseline_cloud),
        variableCloudiness("variable-cloudiness", "nepastoviai debesuota", R.drawable.ic_baseline_cloud),
        cloudyWithSunnyIntervals("cloudy-with-sunny-intervals", "debesuota su pragiedruliais", R.drawable.ic_baseline_cloud),
        cloudy("cloudy", "debesuota", R.drawable.ic_baseline_cloud),
        thunder("thunder", "perkūnija", R.drawable.ic_baseline_cloud),
        isolatedThunderstorms("isolated-thunderstorms", "trumpas lietus su perkūnija", R.drawable.ic_baseline_cloud),
        thunderstorms("thunderstorms", "lietus su perkūnija", R.drawable.ic_baseline_cloud),
        lightRain("light-rain", "nedidelis lietus", R.drawable.ic_baseline_cloud),
        rain("rain", "lietus", R.drawable.ic_baseline_cloud),
        heavyRain("heavy-rain", "smarkus lietus", R.drawable.ic_baseline_cloud),
        rainShowers("rain-showers", "trumpas lietus", R.drawable.ic_baseline_cloud),
        lightRainAtTimes("light-rain-at-times", "protarpiais nedidelis lietus", R.drawable.ic_baseline_cloud),
        rainAtTimes("rain-at-times", "protarpiais lietus", R.drawable.ic_baseline_cloud),
        lightSleet("light-sleet", "nedidelė šlapdriba", R.drawable.ic_baseline_cloud),
        sleet("sleet", "šlapdriba", R.drawable.ic_baseline_cloud),
        sleetAtTimes("sleet-at-times", "protarpiais šlapdriba", R.drawable.ic_baseline_cloud),
        sleetShowers("sleet-showers", "trumpa šlapdriba", R.drawable.ic_baseline_cloud),
        freezingRain("freezing-rain", "lijundra", R.drawable.ic_baseline_cloud),
        hail("hail", "kruša", R.drawable.ic_baseline_grain),
        lightSnow("light-snow", "nedidelis sniegas", R.drawable.ic_baseline_grain),
        snow("snow", "sniegas", R.drawable.ic_baseline_grain),
        heavySnow("heavy-snow", "smarkus sniegas", R.drawable.ic_baseline_grain),
        snowShowers("snow-showers", "trumpas sniegas", R.drawable.ic_baseline_grain),
        snowAtTimes("snow-at-times", "protarpiais sniegas", R.drawable.ic_baseline_grain),
        lightSnowAtTimes("light-snow-at-times", "protarpiais nedidelis sniegas", R.drawable.ic_baseline_grain),
        snowstorm("snowstorm", "pūga", R.drawable.ic_baseline_grain),
        mist("mist", "rūkana", R.drawable.ic_baseline_cloud),
        fog("fog", "rūkas", R.drawable.ic_baseline_cloud),
        squall("squall", "škvalas", R.drawable.ic_baseline_cloud),
        nullValue("null", "oro sąlygos nenustatytos", R.drawable.ic_baseline_cloud);

        private static final Map<String, ConditionCodeTypes> BY_ENGLISH = new HashMap<>();
        private static final Map<String, ConditionCodeTypes> BY_LITHUANIAN = new HashMap<>();
        private static final Map<Integer, ConditionCodeTypes> BY_RESOURCES = new HashMap<>();

        static {
            for (ConditionCodeTypes e : values()) {
                BY_ENGLISH.put(e.english, e);
                BY_LITHUANIAN.put(e.lithuanian, e);
                BY_RESOURCES.put(e.resourceId, e);
            }
        }

        public final String english;
        public final String lithuanian;
        public final int resourceId;

        private ConditionCodeTypes(String english, String lithuanian, int resourceId) {
            this.english = english;
            this.lithuanian = lithuanian;
            this.resourceId = resourceId;
        }

        public static ConditionCodeTypes valueOfEnglish(String word) {
            return BY_ENGLISH.get(word);
        }

        public static ConditionCodeTypes valueOfLithuania(String word) {
            return BY_LITHUANIAN.get(word);
        }

        public static ConditionCodeTypes valueOfResources(String word) {
            return BY_RESOURCES.get(word);
        }
    }

    public ForecastTimestamp(String forecastTimeUtc, float airTemperature, float feelsLikeTemperature, int windSpeed, int windGust, int windDirection, int cloudCover, int seaLevelPressure, int relativeHumidity, float totalPrecipitation, String conditionCode)
    {
        this.forecastTimeUtc = forecastTimeUtc;
        this.airTemperature = airTemperature;
        this.feelsLikeTemperature = feelsLikeTemperature;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
        this.windDirection = windDirection;
        this.cloudCover = cloudCover;
        this.seaLevelPressure = seaLevelPressure;
        this.relativeHumidity = relativeHumidity;
        this.totalPrecipitation = totalPrecipitation;
        this.conditionCode = conditionCode;
    }

    public String getForecastTimeUtc()
    {
        return forecastTimeUtc;
    }

    public void setForecastTimeUtc(String forecastTimeUtc)
    {
        this.forecastTimeUtc = forecastTimeUtc;
    }

    public float getAirTemperature()
    {
        return airTemperature;
    }

    public void setAirTemperature(int airTemperature)
    {
        this.airTemperature = airTemperature;
    }

    public float getFeelsLikeTemperature()
    {
        return feelsLikeTemperature;
    }

    public void setFeelsLikeTemperature(float feelsLikeTemperature)
    {
        this.feelsLikeTemperature = feelsLikeTemperature;
    }

    public int getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed)
    {
        this.windSpeed = windSpeed;
    }

    public int getWindGust()
    {
        return windGust;
    }

    public void setWindGust(int windGust)
    {
        this.windGust = windGust;
    }

    public int getWindDirection()
    {
        return windDirection;
    }

    public void setWindDirection(int windDirection)
    {
        this.windDirection = windDirection;
    }

    public int getCloudCover()
    {
        return cloudCover;
    }

    public void setCloudCover(int cloudCover)
    {
        this.cloudCover = cloudCover;
    }

    public int getSeaLevelPressure()
    {
        return seaLevelPressure;
    }

    public void setSeaLevelPressure(int seaLevelPressure)
    {
        this.seaLevelPressure = seaLevelPressure;
    }

    public int getRelativeHumidity()
    {
        return relativeHumidity;
    }

    public void setRelativeHumidity(int relativeHumidity)
    {
        this.relativeHumidity = relativeHumidity;
    }

    public float getTotalPrecipitation()
    {
        return totalPrecipitation;
    }

    public void setTotalPrecipitation(int totalPrecipitation)
    {
        this.totalPrecipitation = totalPrecipitation;
    }

    public String getConditionCode()
    {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode)
    {
        this.conditionCode = conditionCode;
    }
}
