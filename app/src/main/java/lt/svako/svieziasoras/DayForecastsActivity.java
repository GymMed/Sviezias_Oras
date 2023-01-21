package lt.svako.svieziasoras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lt.svako.svieziasoras.databinding.ActivityDayForecastsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DayForecastsActivity extends AppCompatActivity
{
    private String forecastsDateString;
    private ActivityDayForecastsBinding binding;
    private PostPlaces place;
    private PostPlacesForecasts postPlacesForecasts;
    private List<ForecastTimestamp> dayForecasts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityDayForecastsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setUpInfo();
        setUpScene();
    }

    private void setUpInfo()
    {
        Context context = DayForecastsActivity.this.getApplicationContext();

        postPlacesForecasts = MainActivity.getCachedSingleInfo(context,
                PostPlacesForecasts.getPlaceForecastsSharedPreferencesString(), PostPlacesForecasts.class);

        place = MainActivity.getCachedSingleInfo(context,
                PostPlaces.getPlaceSharedPreferencesString(), PostPlaces.class);

        if (postPlacesForecasts == null) {
            getPlaceForecasts(place.getCode(), MainActivity.getDefaultForecastType());
            //PostPlacesForecasts.setExpiryDate(getNewExpiryDate());
        } else {
            if (!place.getCode().equals(postPlacesForecasts.getPlace().getCode())) {
                getPlaceForecasts(place.getCode(), MainActivity.getDefaultForecastType());
            } else {
                Date todayDate = new Date();
                Date expiryDate = MainActivity.getCachedSingleInfo(context,
                        PostPlacesForecasts.getPlaceForecastsExpiryDateSharedPreferencesString(),
                        Date.class);

                if (expiryDate != null) {
                    if (todayDate.after(expiryDate)) {
                        getPlaceForecasts(place.getCode(), MainActivity.getDefaultForecastType());
                    } else {
                        setUpDayInfo();
                    }
                } else {
                    getPlaceForecasts(place.getCode(), MainActivity.getDefaultForecastType());
                }
            }
        }
    }

    private void setUpScene()
    {


        /*HorizontalScrollView scrollView = binding.hourHScroll;
        LinearLayout linearLayout = binding.hourLinearLayout;

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                int scrollWidth = scrollView.getWidth();
                int insertElementWidth;

                if(linearLayout.getChildAt(0) != null)
                {
                    insertElementWidth = linearLayout.getChildAt(0).getWidth();
                }
                else
                {
                    View view = getLayoutInflater().inflate(R.layout.day_cast, null);
                    ViewGroup dayBox = (ViewGroup) view;
                    insertElementWidth = dayBox.getWidth();
                }

                int fillAmount = scrollWidth / insertElementWidth;
                int uploadNewEvery = fillAmount / 2;

                if(dayForecasts.size() > fillAmount - 1)
                {
                    linearLayout.addView();
                }


                Log.d("scroliukas", Integer.toString(fillAmount));
*/
                /*int scrollX = scrollView.getScrollX();
                if (scrollX == 0) {
                    scrollView.scrollTo(linearLayout.getWidth(), 0);
                } else if (scrollX == linearLayout.getWidth() - scrollView.getWidth()) {
                    scrollView.scrollTo(0, 0);
                }*/
        /*
            }
        });*/
    }

    /*private void fillLinearLayoutEnd(LinearLayout linearLayout, int amount, int startIndex, int maxIndex)
    {

        for(int currentHour = startIndex; currentHour < amount; currentHour++)
        {
            View view = getLayoutInflater().inflate(R.layout.day_cast, null);
            ViewGroup dayBox = (ViewGroup) view;

            TextView text = (TextView) dayBox.getChildAt(0);
            text.setText(Integer.toString(currentHour));

            linearLayout.addView(view, 0);
        }
    }

    private void fillLinearLayoutStart(LinearLayout linearLayout, int amount, int startIndex, int minIndex)
    {
        for(int currentHour = amount; currentHour > 0; currentHour--)
        {
            View view = getLayoutInflater().inflate(R.layout.day_cast, null);
            ViewGroup dayBox = (ViewGroup) view;

            TextView text = (TextView) dayBox.getChildAt(0);
            text.setText(Integer.toString(currentHour));

            linearLayout.addView(view, 0);
        }
    }*/

    private void setUpDayInfo()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null ) {

            if(extras.containsKey("dayForecasts")) {
                dayForecasts = (List<ForecastTimestamp>) extras.getSerializable("dayForecasts");

                setUpHourCastElements();
                //cacheSingleInfo(MainActivity.this.getApplicationContext(), placeInfo, place);
            }
            else
            {
                if(postPlacesForecasts != null)
                {

                    if(forecastsDateString != null) {
                        postPlacesForecasts.getForecastsFromStringDate(forecastsDateString);
                    }
                }
            }

            postPlacesForecasts.fillForecastByDay();

            if(extras.containsKey("dayDate"))
            {
                Date day = (Date) extras.getSerializable("dayDate");
                setDefaultAverageValues(day);
            }
        }
        else
        {

        }
    }

    private void getPlaceForecasts(String placeCode, String forecastType)
    {
        //TextView testText = binding.testText;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meteo.lt/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<PostPlacesForecasts> call = jsonPlaceHolderApi.getPlacesForecasts(placeCode, forecastType);

        call.enqueue(new Callback<PostPlacesForecasts>() {
            @Override
            public void onResponse(Call<PostPlacesForecasts> call, Response<PostPlacesForecasts> response)
            {

                if(!response.isSuccessful())
                {
                    //testText.setText("Code: " + response.code());
                    Log.d("LBS", "Code: " + response.code());
                    return;
                }

                postPlacesForecasts = response.body();

                Log.d("LABAS", response.body().toString());
                //testText.setText(postPlacesForecasts.getPlace().getCountry());
                cachePostPlacesForecasts();

                //setUpWeekCastElements();
                Log.d("retrofitCall", "getPlaceForecasts");
            }

            @Override
            public void onFailure(Call<PostPlacesForecasts> call, Throwable t)
            {
                //testText.setText(t.getMessage());
                Log.d("LBS", t.getMessage());
            }
        });
    }

    private void setUpHourCastElements()
    {
        if(postPlacesForecasts != null && dayForecasts != null)
        {
            int dayForecastsSize = dayForecasts.size();
            int hourDiff = 1;

            SimpleDateFormat exactTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if(dayForecasts.size() > 1)
            {
                Date firstDate = MainActivity.stringToDate(dayForecasts.get(0).getForecastTimeUtc(), exactTime);
                Date secondDate = MainActivity.stringToDate(dayForecasts.get(1).getForecastTimeUtc(), exactTime);

                hourDiff = getHourOfDay(secondDate) - getHourOfDay(firstDate);
            }

            for(int currentHourElement = 0; currentHourElement < dayForecastsSize; currentHourElement++)
            {
                View view = getLayoutInflater().inflate(R.layout.day_cast, null);
                ViewGroup dayBox = (ViewGroup) view;

                Button hourButton = (Button) dayBox.getChildAt(0);
                hourButton.setText(Integer.toString(currentHourElement * hourDiff));

                setHourBoxOnClickEvent(hourButton, MainActivity.stringToDate(dayForecasts.get(currentHourElement).getForecastTimeUtc(), exactTime), dayForecasts.get(currentHourElement));

                binding.hourLinearLayout.addView(view);
            }
        }
    }

    int getHourOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private void setHourBoxOnClickEvent(final Button btn,  final Date day, final ForecastTimestamp dayTimestamp)
    {
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setValues(day, dayTimestamp);
            }
        });
    }

    private void setValues(Date day, ForecastTimestamp timestamp)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH");
        String date = formatter.format(day);
        ForecastTimestamp.ConditionCodeTypes conditionType = ForecastTimestamp.ConditionCodeTypes.valueOfEnglish(timestamp.getConditionCode());

        binding.dayInfoTextView.setText(date + " val. informacija");
        binding.temperatureTextView.setText("Oro temperatūra: " + String.format("%.2f", timestamp.getAirTemperature()) + " \u00B0C");
        binding.feelTemperatureTextView.setText("Juntamoji temperatūra: " + String.format("%.2f", timestamp.getFeelsLikeTemperature()) + " \u00B0C");
        binding.windSpeedTextView.setText("Vėjo greitis: " + Integer.toString(timestamp.getWindSpeed()) + " m/s");
        binding.windGustTextView.setText("Vėjo gūsis: " + Integer.toString(timestamp.getWindGust()) + " m/s");
        binding.windDirectionTextView.setText("Vėjo kryptis: " + Integer.toString(timestamp.getWindDirection()) + " \u00B0");
        binding.cloudCoverTextView.setText("Debesuotumas: " + Integer.toString(timestamp.getCloudCover()) + " %");
        binding.pressureTextView.setText("Slėgis jūros lygyje: " + Integer.toString(timestamp.getSeaLevelPressure()) + " hPa");
        binding.humidityTextView.setText("Santykinė oro drėgmė: " + Integer.toString(timestamp.getRelativeHumidity()) + " %");
        binding.precipitationTextView.setText("Kritulių kiekis: " + String.format("%.4f", timestamp.getTotalPrecipitation()) + " mm");
        binding.conditionCodeTextView.setText("Oro apibūdinmas: " + conditionType.lithuanian);
        binding.hourCastImage.setImageResource(conditionType.resourceId);
    }

    private void setDefaultAverageValues(Date day)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(day);
        ForecastTimestamp.ConditionCodeTypes conditionType = ForecastTimestamp.ConditionCodeTypes.valueOfEnglish(postPlacesForecasts.getAverageConditionCodeFromDate(day));

        binding.dayInfoTextView.setText("Bendra " + date + " d. informacija");
        binding.temperatureTextView.setText("Oro temperatūra: " + String.format("%.2f", postPlacesForecasts.getAverageFValueFromDate(day, PostPlacesForecasts.AverageFloatCalculationValues.AirTemperature)) + " \u00B0C");
        binding.feelTemperatureTextView.setText("Juntamoji temperatūra: " + String.format("%.2f", postPlacesForecasts.getAverageFValueFromDate(day, PostPlacesForecasts.AverageFloatCalculationValues.FeelsLikeTemperature)) + " \u00B0C");
        binding.windSpeedTextView.setText("Vėjo greitis: " + Integer.toString(postPlacesForecasts.getAverageValueFromDate(day, PostPlacesForecasts.AverageIntCalculationValues.WindSpeed)) + " m/s");
        binding.windGustTextView.setText("Vėjo gūsis: " + Integer.toString(postPlacesForecasts.getAverageValueFromDate(day, PostPlacesForecasts.AverageIntCalculationValues.WindGust)) + " m/s");
        binding.windDirectionTextView.setText("Vėjo kryptis: " + Integer.toString(postPlacesForecasts.getAverageValueFromDate(day, PostPlacesForecasts.AverageIntCalculationValues.WindDirection)) + " \u00B0");
        binding.cloudCoverTextView.setText("Debesuotumas: " + Integer.toString(postPlacesForecasts.getAverageValueFromDate(day, PostPlacesForecasts.AverageIntCalculationValues.CloudCover)) + " %");
        binding.pressureTextView.setText("Slėgis jūros lygyje: " + Integer.toString(postPlacesForecasts.getAverageValueFromDate(day, PostPlacesForecasts.AverageIntCalculationValues.SeaLevelPressure)) + " hPa");
        binding.humidityTextView.setText("Santykinė oro drėgmė: " + Integer.toString(postPlacesForecasts.getAverageValueFromDate(day, PostPlacesForecasts.AverageIntCalculationValues.RelativeHumidity)) + " %");
        binding.precipitationTextView.setText("Kritulių kiekis: " + String.format("%.4f", postPlacesForecasts.getAverageFValueFromDate(day, PostPlacesForecasts.AverageFloatCalculationValues.TotalPrecipitation)) + " mm");
        binding.conditionCodeTextView.setText("Oro apibūdinmas: " + conditionType.lithuanian);
        binding.hourCastImage.setImageResource(conditionType.resourceId);
    }

    private void cachePostPlacesForecasts()
    {
        Context context = DayForecastsActivity.this.getApplicationContext();

        MainActivity.cacheSingleInfo(context,
                PostPlacesForecasts.getPlaceForecastsExpiryDateSharedPreferencesString(), MainActivity.getNewExpiryDate());
        MainActivity.cacheSingleInfo(context,
                PostPlacesForecasts.getPlaceForecastsSharedPreferencesString(), postPlacesForecasts);
    }
}