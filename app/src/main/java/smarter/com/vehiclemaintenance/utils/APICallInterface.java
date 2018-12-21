package smarter.com.vehiclemaintenance.utils;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by MrBrown on 1/29/2018.
 */

public interface APICallInterface {

    @GET @Headers("Connection:close")
    Call<ResponseBody> callAPI(@Url String url);

    @GET @Headers("Connection:close")
    Call<ResponseBody> callAPI(@Url String url, @QueryMap Map<String, String> parameters);

    @POST @Headers("Connection:close")
    Call<ResponseBody> postToAPI(@Url String url, @Body RequestBody parameter);

    /*extra*/
    @GET @Headers("Connection:close")
    Call<ResponseBody> getAPI(@Url String url, @Body RequestBody parameter);

    @POST @Headers("Connection:close")
    Call<ResponseBody> postAPI(@Url String url, @QueryMap Map<String, String> parameters);

    @POST @Headers("Connection:close")
    Call<ResponseBody> postToAPI(@Url String url);


}
