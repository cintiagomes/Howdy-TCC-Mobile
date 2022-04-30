package com.example.howdy.remote;

import com.example.howdy.model.MySqlResult;
import com.example.howdy.model.User;
import com.example.howdy.model.UserCreation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RouterInterface {

    /** ROTAS DE USUÁRIO **/
    @POST("/users")
    Call<MySqlResult> createUser(@Body UserCreation user, @Header("Authorization") String idToken);

    @GET("/users/isMyUidExternalRegistered")
    Call<List<User>>isMyUidExternalRegistered(@Header("Authorization") String idToken);
}