package lt.svako.svieziasoras;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lt.svako.svieziasoras.databinding.ActivityChoosePlaceBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChoosePlaceActivity extends AppCompatActivity {

    private ActivityChoosePlaceBinding binding;
    private PostPlaces place = null;

    private List<PostPlaces> posts = new ArrayList<>();
    private ArrayList<String> placesList = new ArrayList<>();
    private ArrayAdapter<String> placeAdapter;

    private int selecetedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChoosePlaceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setUpInfo();
        setUpScene();
    }

    private void setUpInfo()
    {
        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            place = (PostPlaces) extras.getSerializable(PostPlaces.getPlaceSharedPreferencesString());
        }

        Context context = ChoosePlaceActivity.this.getApplicationContext();
        posts = MainActivity.getCachedMultipleInfo(context, PostPlaces.getListPlaceSharedPreferencesString(),
                PostPlaces[].class);

        if(posts == null) {
            getPlaces();
        }

        Date todayDate = new Date();
        Date expiryDate = MainActivity.getCachedSingleInfo(context,
                PostPlaces.getListExpiryDatePlaceSharedPreferencesString(),
                Date.class);

        if (expiryDate != null)
        {
            if(todayDate.after(expiryDate))
            {
                getPlaces();
            }
            else
            {
                setUpPlacesAdapter();
            }
        }
        else
        {
            getPlaces();
        }
    }

    private void cachePostPlaces()
    {
        Context context = ChoosePlaceActivity.this.getApplicationContext();

        MainActivity.cacheSingleInfo(context,
                PostPlaces.getListExpiryDatePlaceSharedPreferencesString(), MainActivity.getNewExpiryDate());
        MainActivity.cacheSingleInfo(context,
                PostPlaces.getListPlaceSharedPreferencesString(), posts);
    }

    private void setUpScene()
    {
        if(place != null)
        {
            setChosenPlace(place);
        }

        binding.choosePlaceSaveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(ChoosePlaceActivity.this, MainActivity.class);

                if(posts.size() > selecetedItem)
                {
                    //intent.putExtra("placeInfo", posts.get(selecetedItem));
                    PostPlaces selectedPost = posts.get(selecetedItem);

                    //PostPlaces.setExpiryDate(MainActivity.getNewExpiryDate());
                    MainActivity.cacheSingleInfo(ChoosePlaceActivity.this.getApplicationContext(),
                            PostPlaces.getPlaceSharedPreferencesString(), selectedPost);
                }

                startActivity(intent);
            }
        });
    }

    private void setChosenPlace(PostPlaces pickedPlace)
    {
        binding.choosePlaceAutoCompleteTextView.setText(pickedPlace.getName());
        binding.placeCodeTextView.setText("Vietovės kodas: " + pickedPlace.getCode());
        binding.placeNameTextView.setText("Vietovės pavadinimas: " + pickedPlace.getName());
        binding.administrativeDivisionTextView.setText("Administracinis vienetas, kuriam priklauso vietovė: " + pickedPlace.getAdministrativeDivision());
        binding.countryCodeTextView.setText("Šalies kodas(ISO 3166-1 alpha-2): " + pickedPlace.getCountryCode());
    }

    private void getPlaces()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meteo.lt/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List<PostPlaces>> call = jsonPlaceHolderApi.getPlaces();

        call.enqueue(new Callback<List<PostPlaces>>() {
            @Override
            public void onResponse(Call<List<PostPlaces>> call, Response<List<PostPlaces>> response) {

                if (!response.isSuccessful()) {
                    Log.d("LABAS", "Code: " + response.code());
                    return;
                }

                posts = response.body();

                //Log.d("LABAS", response.body().toString());
                Log.d("retrofitCall", "getPlaces");
                cachePostPlaces();

                setUpPlacesAdapter();
            }

            @Override
            public void onFailure(Call<List<PostPlaces>> call, Throwable t) {
                Log.d("LBS", t.getMessage());
            }
        });
    }

    private void setUpPlacesAdapter()
    {
        int totalPosts = posts.size();

        for (PostPlaces post : posts) {
            placesList.add(post.getName());//post.getCode()
        }

        placeAdapter = new ArrayAdapter<String>(ChoosePlaceActivity.this, R.layout.item_dropdown, placesList);
        binding.choosePlaceAutoCompleteTextView.setAdapter(placeAdapter);
        binding.choosePlaceAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String typedText = binding.choosePlaceAutoCompleteTextView.getText().toString();

                String lowerTypedText = typedText.toLowerCase();

                for (int currentPost = 0; currentPost < totalPosts; currentPost++) {
                    //lygina ivesta zodi su gautais rezultatais is svetaines
                    //ivestas zodis nebutinai bus pilnas ir gali buti, bet kurioje pozicijoje
                    if (posts.get(currentPost).getName().toLowerCase().contains(lowerTypedText)) {
                        //ivestas zodis nebutinai bus pilnas ir pasirinktas variantas
                        //nebutinai bus pirmas, kadangi position parodo kelintas pasirinkimas
                        //buvo pasirinktas, nuo jo atsiskaiciuosim musu atitikmeni
                        selecetedItem = currentPost;
                        setChosenPlace(posts.get(currentPost));
                        break;
                    }
                }
            }
        });
    }
}