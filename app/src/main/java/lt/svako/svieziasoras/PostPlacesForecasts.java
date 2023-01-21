package lt.svako.svieziasoras;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostPlacesForecasts
{
    private static final String placeForecastsSharedPreferencesString = "placeForecastsInfo";
    private static final String placeForecastsExpiryDateSharedPreferencesString = "placeForecastsExpiryDateInfo";

    private Place place;
    private String forecastType;
    private String forecastCreationTimeUtc;
    private List<ForecastTimestamp> forecastTimestamps;

    private Map<String, List<ForecastTimestamp>> forecastByDayTimestamps;

    public enum AverageFloatCalculationValues
    {
        AirTemperature,
        FeelsLikeTemperature,
        TotalPrecipitation
    }

    public enum AverageIntCalculationValues
    {
        WindSpeed,
        WindGust,
        WindDirection,
        CloudCover,
        SeaLevelPressure,
        RelativeHumidity
    }

    public PostPlacesForecasts(Place place, String forecastType, String forecastCreationTimeUtc, List<ForecastTimestamp> forecastTimestamps)
    {
        this.place = place;
        this.forecastType = forecastType;
        this.forecastCreationTimeUtc = forecastCreationTimeUtc;
        this.forecastTimestamps = forecastTimestamps;
    }

    public static String getPlaceForecastsSharedPreferencesString()
    {
        return placeForecastsSharedPreferencesString;
    }

    public static String getPlaceForecastsExpiryDateSharedPreferencesString()
    {
        return placeForecastsExpiryDateSharedPreferencesString;
    }

    public Place getPlace()
    {
        return place;
    }

    public void setPlace(Place place)
    {
        this.place = place;
    }

    public String getForecastType()
    {
        return forecastType;
    }

    public void setForecastType(String forecastType)
    {
        this.forecastType = forecastType;
    }

    public String getForecastCreationTimeUtc()
    {
        return forecastCreationTimeUtc;
    }

    public void setForecastCreationTimeUtc(String forecastCreationTimeUtc)
    {
        this.forecastCreationTimeUtc = forecastCreationTimeUtc;
    }

    public List<ForecastTimestamp> getForecastTimestamps()
    {
        return forecastTimestamps;
    }

    public void setForecastTimestamps(List<ForecastTimestamp> forecastTimestamps)
    {
        this.forecastTimestamps = forecastTimestamps;
    }

    public void fillForecastByDay()
    {
        int totalTimestamps = forecastTimestamps.size();
        String currentDate = "", lastDate = forecastTimestamps.get(0).getForecastTimeUtc().substring(0, 10), fullDate = "";
        List<ForecastTimestamp> forecastTsList = new ArrayList<ForecastTimestamp>();
        forecastByDayTimestamps = new HashMap<>();

        for (int currentTimestamp = 0; currentTimestamp < totalTimestamps; currentTimestamp++)
        {
            fullDate = forecastTimestamps.get(currentTimestamp).getForecastTimeUtc();

            if(fullDate.length() > 10)
            {
                currentDate = fullDate.substring(0, 10);

                if(!lastDate.equals(currentDate))
                {
                    forecastByDayTimestamps.put(lastDate, forecastTsList);

                    lastDate = currentDate;
                    forecastTsList = new ArrayList<>();
                    forecastTsList.add(forecastTimestamps.get(currentTimestamp));
                }
                else
                {
                    //Log.d("sukutikaasssii", currentDate + " laba " + String.valueOf(currentTimestamp) + " kk "
                    //        + String.valueOf(forecastTimestamps.get(currentTimestamp).getAirTemperature())
                    //        + " kaka " + String.valueOf(forecastTimestamps.get(totalTimestamps - 1).getForecastTimeUtc()));
                    forecastTsList.add(forecastTimestamps.get(currentTimestamp));
                }

                //jei kartojasi paskutine diena, kai pripildom sarasa issaugom ir ji
                if(currentTimestamp == totalTimestamps - 1)
                {
                    forecastByDayTimestamps.put(currentDate, forecastTsList);
                }
            }
        }
    }

    public List<ForecastTimestamp> getForecastsFromStringDate(String date)
    {
        List<ForecastTimestamp> forecastBySentDay = null;

        if(forecastTimestamps.size() > 0)
        {
            forecastBySentDay = forecastByDayTimestamps.get(date);
        }

        return forecastBySentDay;
    }

    public List<ForecastTimestamp> getForecastsFromDate(Date date)
    {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date forecastDate;
        List<ForecastTimestamp> forecastBySentDay = null;

        if(forecastTimestamps.size() > 0)
        {
            for(Map.Entry<String, List<ForecastTimestamp>> forecastByDayTs : forecastByDayTimestamps.entrySet())
            {
                try
                {
                    forecastDate = dateFormater.parse(forecastByDayTs.getKey());

                    if(forecastDate.compareTo(date) == 0)
                    {
                        forecastBySentDay = forecastByDayTs.getValue();
                        break;
                    }
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return forecastBySentDay;
    }

    public String getAverageConditionCodeFromDate(Date date)
    {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date forecastDate;
        String winningCondition = "null";

        if(forecastTimestamps.size() > 0)
        {
            int totalSentDayTs = 0;
            int mostRepeats = 0, currentConditionRepeats;

            Map<String, Integer> conditionCodeFindings = new HashMap<String, Integer>();
            String conditionCodeChecking;
            List<ForecastTimestamp> forecastBySentDay;

            for(Map.Entry<String, List<ForecastTimestamp>> forecastByDayTs : forecastByDayTimestamps.entrySet())
            {
                try
                {
                    forecastDate = dateFormater.parse(forecastByDayTs.getKey());

                    if(forecastDate.compareTo(date) == 0)
                    {
                        forecastBySentDay = forecastByDayTs.getValue();
                        totalSentDayTs = forecastBySentDay.size();

                        for(int currentTs = 0; currentTs < totalSentDayTs; currentTs++)
                        {
                            conditionCodeChecking = forecastBySentDay.get(currentTs).getConditionCode();

                            if(conditionCodeFindings.containsKey(conditionCodeChecking))
                            {
                                currentConditionRepeats = conditionCodeFindings.get(conditionCodeChecking) + 1;
                                conditionCodeFindings.put(conditionCodeChecking, currentConditionRepeats);
                            }
                            else
                            {
                                currentConditionRepeats = 1;
                                conditionCodeFindings.put(conditionCodeChecking, currentConditionRepeats);
                            }

                            //nustatom nauja didziausia pasikartojancia oro kondicija
                            //else atvejis pirmam kartui svarbus virsuje
                            if(mostRepeats < currentConditionRepeats)
                            {
                                mostRepeats = currentConditionRepeats;
                                winningCondition = conditionCodeChecking;
                                //Log.d("conditions", conditionCodeChecking + " " + Integer.toString(mostRepeats));
                            }
                        }
                        break;
                    }
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return winningCondition;
    }

    public float getAverageFValueFromDate(Date date, AverageFloatCalculationValues fvalue)
    {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date forecastDate;

        if(forecastTimestamps.size() > 0)
        {
            int totalSentDayTs = 0;
            float allValues = 0.0f;
            List<ForecastTimestamp> forecastBySentDay;

            for(Map.Entry<String, List<ForecastTimestamp>> forecastByDayTs : forecastByDayTimestamps.entrySet())
            {
                try
                {
                    forecastDate = dateFormater.parse(forecastByDayTs.getKey());

                    if(forecastDate.compareTo(date) == 0)
                    {
                        forecastBySentDay = forecastByDayTs.getValue();
                        totalSentDayTs = forecastBySentDay.size();

                        for(int currentTs = 0; currentTs < totalSentDayTs; currentTs++)
                        {
                            allValues += getFValueForCalculation(forecastBySentDay.get(currentTs), fvalue);
                        }
                        break;
                    }
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
            //Log.d("sukutika", "hi " + String.valueOf(allValues) + " by " + String.valueOf(totalSentDayTs) + " ha " +
            //        String.valueOf(date) + " nice " + String.valueOf(allValues / totalSentDayTs) );
            return allValues / totalSentDayTs;
        }

        return -999999.999999f;
    }

    public int getAverageValueFromDate(Date date, AverageIntCalculationValues value)
    {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Date forecastDate;

        if(forecastTimestamps.size() > 0)
        {
            int totalSentDayTs = 0;
            int allValues = 0;
            List<ForecastTimestamp> forecastBySentDay;

            for(Map.Entry<String, List<ForecastTimestamp>> forecastByDayTs : forecastByDayTimestamps.entrySet())
            {
                try
                {
                    forecastDate = dateFormater.parse(forecastByDayTs.getKey());

                    if(forecastDate.compareTo(date) == 0)
                    {
                        forecastBySentDay = forecastByDayTs.getValue();
                        totalSentDayTs = forecastBySentDay.size();

                        for(int currentTs = 0; currentTs < totalSentDayTs; currentTs++)
                        {
                            allValues += getValueForCalculation(forecastBySentDay.get(currentTs), value);
                        }
                        break;
                    }
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
            //Log.d("sukutika", "hi " + String.valueOf(allValues) + " by " + String.valueOf(totalSentDayTs) + " ha " +
            //        String.valueOf(date) + " nice " + String.valueOf(allValues / totalSentDayTs) );
            return allValues / totalSentDayTs;
        }

        return -999999;
    }

    private float getFValueForCalculation(ForecastTimestamp timestamp, AverageFloatCalculationValues fvalue)
    {
        switch(fvalue)
        {
            case AirTemperature:
            {
                return timestamp.getAirTemperature();
            }
            case FeelsLikeTemperature:
            {
                return timestamp.getFeelsLikeTemperature();
            }
            case TotalPrecipitation:
            {
                return timestamp.getTotalPrecipitation();
            }
            default:
            {
                return timestamp.getAirTemperature();
            }
        }
    }

    private int getValueForCalculation(ForecastTimestamp timestamp, AverageIntCalculationValues value)
    {
        switch(value)
        {
            case WindSpeed:
            {
                return timestamp.getWindSpeed();
            }
            case WindGust:
            {
                return timestamp.getWindGust();
            }
            case WindDirection:
            {
                return timestamp.getWindDirection();
            }
            case CloudCover:
            {
                return timestamp.getCloudCover();
            }
            case SeaLevelPressure:
            {
                return timestamp.getSeaLevelPressure();
            }
            case RelativeHumidity:
            {
                return timestamp.getRelativeHumidity();
            }
            default:
            {
                return timestamp.getWindSpeed();
            }
        }
    }
}
