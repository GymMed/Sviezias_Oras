package lt.svako.svieziasoras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lt.svako.svieziasoras.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int defaultExpiryHours = 3;
    private static final String defaultForecastType = "long-term";

    private ActivityMainBinding binding;
    private PostPlaces place;
    private PostPlacesForecasts postPlacesForecasts;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public enum WeekCastUiElements
    {
        WeekCastButton,
        DayText,
        DateText,
        TemperatureText,
        WeekCastImage
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setUpInfo();
        setUpScene();
    }

    private void setUpInfo() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Context context = MainActivity.this.getApplicationContext();

        if(place == null) {
            String placeInfo = PostPlaces.getPlaceSharedPreferencesString();

            /*
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(placeInfo)) {
                place = (PostPlaces) extras.getSerializable(placeInfo);
                cacheSingleInfo(MainActivity.this.getApplicationContext(), placeInfo, place);
            }
            else {
            }*/
            place = getCachedSingleInfo(context, placeInfo, PostPlaces.class);

            if (place == null) {
                getLocation();
                place = new PostPlaces("vilnius", "Vilnius", "Vilniaus miesto savivaldybė", "LT");
            }
        }
        binding.choosePalceButton.setText(place.getName());

        postPlacesForecasts = getCachedSingleInfo(context,
                PostPlacesForecasts.getPlaceForecastsSharedPreferencesString(), PostPlacesForecasts.class);

        if(postPlacesForecasts == null) {
            getPlaceForecasts(place.getCode(), defaultForecastType);
            //PostPlacesForecasts.setExpiryDate(getNewExpiryDate());
        }
        else {
            if (!place.getCode().equals( postPlacesForecasts.getPlace().getCode() )) {
                getPlaceForecasts(place.getCode(), defaultForecastType);
            } else {
                Date todayDate = new Date();
                Date expiryDate = getCachedSingleInfo(context,
                        PostPlacesForecasts.getPlaceForecastsExpiryDateSharedPreferencesString(),
                        Date.class);

                if (expiryDate != null) {
                    if (todayDate.after(expiryDate)) {
                        getPlaceForecasts(place.getCode(), defaultForecastType);
                    } else {
                        setUpWeekCastElements();
                    }
                } else {
                    getPlaceForecasts(place.getCode(), defaultForecastType);
                }
            }
        }
    }

    private void cachePostPlacesForecasts()
    {
        Context context = MainActivity.this.getApplicationContext();

        MainActivity.cacheSingleInfo(context,
                PostPlacesForecasts.getPlaceForecastsExpiryDateSharedPreferencesString(), MainActivity.getNewExpiryDate());
        MainActivity.cacheSingleInfo(context,
                PostPlacesForecasts.getPlaceForecastsSharedPreferencesString(), postPlacesForecasts);
    }

    public static Date getNewExpiryDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, defaultExpiryHours);
        //cal.add(Calendar.MILLISECOND, defaultExpiryHours);
        return cal.getTime();
    }

    public static <Thing> void cacheSingleInfo(Context context, String saveAsName, Thing saveClass)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String jsonString = new GsonBuilder().serializeNulls().create().toJson(saveClass);

        editor.putString(saveAsName, jsonString);
        //sync way
        editor.commit();
        //async way
        //editor.apply();
    }

    public static <Thing> Thing getCachedSingleInfo(Context context, String storedInfoName, Class<Thing> storedClass)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);
        Thing receivedInfo = null;

        if(sharedPreferences != null)
        {
            String storedInfo = sharedPreferences.getString(storedInfoName, "");

            if(storedInfo != null)
            {
                Gson gson = new Gson();
                //Type type = new TypeToken<Thing>(){}.getType();
                receivedInfo = gson.fromJson(storedInfo, storedClass);
            }
        }

        return receivedInfo;
    }

    public static <Thing> List<Thing> getCachedMultipleInfo(Context context, String storedInfoName, Class<Thing[]> storedClass)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);
        List<Thing> receivedInfo = null;

        if(sharedPreferences != null)
        {
            String storedInfo = sharedPreferences.getString(storedInfoName, "");

            if(storedInfo != null)
            {
                Gson gson = new GsonBuilder().serializeNulls().create();
                //Type type = new TypeToken<Thing>(){}.getType();
                Thing[] receivedArray = gson.fromJson(storedInfo, storedClass);

                if(receivedArray != null) {
                    receivedInfo = Arrays.asList(receivedArray);
                }
            }
        }

        return receivedInfo;
    }

    public static void clearAllCache(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("general", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
    }

    public static String getDefaultForecastType()
    {
        return defaultForecastType;
    }

    private void getLocation()
    {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 50);

                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();

                        if (location != null) {
                            Geocoder geo = new Geocoder(MainActivity.this, Locale.getDefault());

                            try
                            {
                                List<Address> addresses = geo.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                );

                                Log.d("LAka", addresses.get(0).getCountryName() + " " + addresses.get(0).getLocality());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
            }
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void setUpWeekCastElements()
    {
        //SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat exactTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat exactToDayFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dayOfTheWeek = new SimpleDateFormat("EEEE");

        //List<>

        if(postPlacesForecasts != null)
        {
            postPlacesForecasts.fillForecastByDay();
            List<ForecastTimestamp> timestamps = postPlacesForecasts.getForecastTimestamps();

            int totalTimestamps = timestamps.size();
            Date dateTimestamp, exactDate;
            String currentWeekDay = "", lastWeekDay = "", fullDate = "", shortDate;

            for (int currentTimestamp = 0; currentTimestamp < totalTimestamps; currentTimestamp++)
            {
                View view = getLayoutInflater().inflate(R.layout.week_cast, null);
                ViewGroup dayBox = (ViewGroup) view;

                Button dayBoxButton = (Button) dayBox.getChildAt(WeekCastUiElements.WeekCastButton.ordinal());
                fullDate = timestamps.get(currentTimestamp).getForecastTimeUtc();
                exactDate = stringToDate(fullDate, exactToDayFormat);

                setDayBoxOnClickEvent(dayBoxButton, exactDate, postPlacesForecasts.getForecastsFromDate(exactDate));

                TextView weekDayTextView = (TextView) dayBox.getChildAt(WeekCastUiElements.DayText.ordinal());
                TextView dateTextView = (TextView) dayBox.getChildAt(WeekCastUiElements.DateText.ordinal());
                TextView temperatureTextView = (TextView) dayBox.getChildAt(WeekCastUiElements.TemperatureText.ordinal());
                ImageView conditionImageView = (ImageView) dayBox.getChildAt(WeekCastUiElements.WeekCastImage.ordinal());

                dateTimestamp = stringToDate(fullDate, exactTime);

                if(dateTimestamp != null)
                {
                    currentWeekDay = dayOfTheWeek.format(dateTimestamp);

                    //Kadangi api grazina duomenis, kurie eina tik i prieki ir
                    //mes norim israsyti tik unikales dienas, tai mes tikrinam
                    //tik paskutines dienos informacija
                    if(!lastWeekDay.equals(currentWeekDay))
                    {
                        lastWeekDay = currentWeekDay;

                        weekDayTextView.setText(currentWeekDay.substring(0, 1).toUpperCase() + currentWeekDay.substring(1));
                        shortDate = fullDate.substring(0, 10);
                        dateTextView.setText(shortDate);

                        float averageTemperature = 0.0f;

                        averageTemperature = postPlacesForecasts.getAverageFValueFromDate(exactDate, PostPlacesForecasts.AverageFloatCalculationValues.AirTemperature);
                        //exactToDayFormat.parse(shortDate)//old way with try parse check stringToDate()

                        temperatureTextView.setText(String.format("%.1f", averageTemperature) + " \u00B0C");
                        conditionImageView.setImageResource(ForecastTimestamp.ConditionCodeTypes.valueOfEnglish(postPlacesForecasts.getAverageConditionCodeFromDate(exactDate)).resourceId);

                        binding.weekCastLayout.addView(view);
                    }
                }
                else
                {
                    //klaidu israsymui riektu log dokumento
                    weekDayTextView.setText("error with timestamps");
                    binding.weekCastLayout.addView(view);
                }
            }
        }
    }

    private void setUpScene()
    {
        binding.choosePalceButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, ChoosePlaceActivity.class);
                intent.putExtra(PostPlaces.getPlaceSharedPreferencesString(), place);
                startActivity(intent);
            }
        });
    }

    public static Date stringToDate(String dateText, SimpleDateFormat simpleDateFormat)
    {
        Date dateTime = null;

        try
        {
            dateTime = simpleDateFormat.parse(dateText);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateTime;
    }

    private void getPlaceForecasts(String placeCode, String forecastType)
    {
        TextView testText = binding.testText;

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
                    testText.setText("Code: " + response.code());
                    return;
                }

                postPlacesForecasts = response.body();

                Log.d("LABAS", response.body().toString());
                testText.setText(postPlacesForecasts.getPlace().getCountry());
                cachePostPlacesForecasts();
                setUpWeekCastElements();
                Log.d("retrofitCall", "getPlaceForecasts");
            }

            @Override
            public void onFailure(Call<PostPlacesForecasts> call, Throwable t)
            {
                testText.setText(t.getMessage());
                Log.d("LBS", t.getMessage());
            }
        });
    }

    private void setDayBoxOnClickEvent(final Button btn,  final Date dayOfForecasts, List<ForecastTimestamp> dayTimestamps)
    {
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, DayForecastsActivity.class);
                intent.putExtra("dayForecasts", (Serializable) dayTimestamps);
                intent.putExtra("dayDate", dayOfForecasts);
                startActivity(intent);
            }
        });
    }
}