package lt.svako.svieziasoras;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi
{
    @GET("places")
    Call<List<PostPlaces>> getPlaces();

    @GET("places/{place-code}/forecasts/{forecast-type}/")
    Call<PostPlacesForecasts> getPlacesForecasts(@Path("place-code") String placeCode, @Path("forecast-type") String forecastType);
}
