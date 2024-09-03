package com.fahad.integraatask;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    String BASE_URL = "http://apm.integraaposta.com/gestione/api/";

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(
            @Field("u") String username,
            @Field("p") String password
    );

    @GET("waterPermissions")  // Replace with your actual endpoint
    Call<PermissionResponse> getPermission(@Header("Token") String headerValue);

}
