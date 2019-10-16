package iut.desvignes.mymeteo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by androidS4 on 19/03/18.
 */

public interface OpenWeatherService {
    @GET("weather?APPID=7d4cb01ae28e955cb88aca49a5432d95&units=metric")
        Call<MeteoModel> getTownByName(@Query("q") String townName);

    @GET("weather?APPID=7d4cb01ae28e955cb88aca49a5432d95&units=metric") //TODO pour map getTownByCOORD
    Call<MeteoModel> getTownById(@Query("id") int townId);
}
