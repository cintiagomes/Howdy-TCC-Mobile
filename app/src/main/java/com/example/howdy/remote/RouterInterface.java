package com.example.howdy.remote;

import com.example.howdy.model.MySqlResult;
import com.example.howdy.model.PostTypes.DataToCreatePostWithoutImage;
import com.example.howdy.model.PostTypes.Post;
import com.example.howdy.model.UserTypes.User;
import com.example.howdy.model.UserTypes.UserRegister;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RouterInterface {

    /** ROTAS DE USUÁRIO **/
    //ROTA PARA CADASTRAR USUÁRIO
    @POST("/users")
    Call<MySqlResult> createUser(@Body UserRegister user, @Header("Authorization") String idToken);

    //ROTA PARA CONFERIR SE O USUÁRIO POSSUI UMA RESPECTIVA CONTA NO BANCO SQL
    @GET("/users/isMyUidExternalRegistered")
    Call<List<User>>isMyUidExternalRegistered(@Header("Authorization") String idToken);

    /** ROTAS DE POSTAGENS **/
    //ROTA PARA COLETAR POSTAGENS PÚBLICAS, "categoryFilter" pode ser o id de cada categoria, "popular", ou "myFriends"
    @GET("/posts/category/{categoryFilter}")
    Call<List<Post>>getPublicPosts(@Path("categoryFilter") String categoryFilter, @Header("Authorization") String idToken);

    //ROTA PARA CRIAR POSTAGENS NA AUSÊNCIA DE IMAGEM
    @POST("/posts")
    Call<MySqlResult>createPostWithoutImage(@Header("Authorization") String idToken, @Body DataToCreatePostWithoutImage dataToCreatePost);

    //ROTA PARA CRIAR POSTAGENS NA PRESENÇA DE IMAGEM
    @Multipart
    @POST("/posts")
    Call<MySqlResult>createPostWithImage(
            @Part MultipartBody.Part file,
            @Part("textContent") RequestBody textContent,
            @Part("isPublic") RequestBody isPublic,
            @Part("idPostCategory") RequestBody idPostCategory,
            @Header("Authorization") String idToken
            );
}