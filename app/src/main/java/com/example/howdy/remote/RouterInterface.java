package com.example.howdy.remote;

import com.example.howdy.model.MySqlResult;
import com.example.howdy.model.PostTypes.DataToCreatePostWithoutImage;
import com.example.howdy.model.PostTypes.Post;
import com.example.howdy.model.PostTypes.PostCategory;
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary;
import com.example.howdy.model.PostTypes.PostCommentaryTypes.DataToCreateCommentary;
import com.example.howdy.model.UserTypes.User;
import com.example.howdy.model.UserTypes.DataToCreateUser;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RouterInterface {

    /** ROTAS DE USUÁRIO **/
    //ROTA PARA CADASTRAR USUÁRIO
    @POST("/users")
    Call<MySqlResult> createUser(@Body DataToCreateUser user, @Header("Authorization") String idToken);

    //ROTA PARA CONFERIR SE O USUÁRIO POSSUI UMA RESPECTIVA CONTA NO BANCO SQL
    @GET("/users/isMyUidExternalRegistered")
    Call<List<User>>isMyUidExternalRegistered(@Header("Authorization") String idToken);

    //ROTA PARA DELETAR CONTA DE USUÁRIO
    @DELETE("/users")
    Call<MySqlResult>deleteMyAccountOnBackend(@Header("Authorization") String idToken);

    /** ROTAS DE POSTAGENS **/
    //ROTA PARA COLETAR POSTAGENS PÚBLICAS, "categoryFilter" pode ser o id de cada categoria, "popular", ou "myFriends"
    @GET("/posts/category/{categoryFilter}")
    Call<List<Post>>getPublicPosts(
            @Path("categoryFilter") String categoryFilter, @Header("Authorization") String idToken
    );

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

    //ROTA PARA EDITAR UMA POSTAGEM
    @Multipart
    @PUT("/posts/{idPost}")
    Call<MySqlResult>editPostWithImage(
            @Path("idPost") int idPost,
            @Part MultipartBody.Part file,
            @Part("textContent") RequestBody textContent,
            @Part("isPublic") RequestBody isPublic,
            @Part("idPostCategory") RequestBody idPostCategory,
            @Header("Authorization") String idToken
    );

    //ROTA PARA DELETAR UMA POSTAGEM
    @DELETE("/posts/{idPost}")
    Call<MySqlResult>createPostWithImage(
            @Path("idPost") int idPost, @Header("Authorization") String idToken
    );

    /** ROTAS DE CURTIDAS DE POSTAGENS **/
    //ROTA PARA CURTIR UMA POSTAGEM
    @POST("/posts/like/{idPost}")
    Call<MySqlResult>likePost(
            @Header("Authorization") String idToken,
            @Path("idPost") int idPost
    );

    //ROTA PARA DESCURTIR UMA POSTAGEM
    @DELETE("/posts/like/{idPost}")
    Call<MySqlResult>unlikePost(
            @Header("Authorization") String idToken,
            @Path("idPost") int idPost
    );

    /** ROTAS DE CATEGORIAS DE POSTAGENS **/
    @GET("/postCategories")
    Call<List<PostCategory>>getPostCategories(
            @Header("Authorization") String idToken
    );

    /** ROTAS DE COMENTÁRIOS DE POSTAGENS **/
    //ROTA PARA OBTER OS COMENTÁRIOS DE UMA POSTAGEM
    @GET("/posts/commentary/{idPost}")
    Call<List<Commentary>>getPostComments(
            @Header("Authorization") String idToken,
            @Path("idPost") int idPost
    );

    //ROTA PARA EDITAR UM COMENTÁRIO
    @PUT("/posts/commentary/{idPostCommentary}")
    Call<MySqlResult>editPostCommentary(
            @Header("Authorization") String idToken,
            @Path("idPostCommentary") int idPostCommentary,
            @Body DataToCreateCommentary commentary
    );

    //ROTA PARA CRIAR UM COMENTÁRIO
    @POST("/posts/commentary/{idPost}")
    Call<MySqlResult>commentPost(
            @Header("Authorization") String idToken,
            @Path("idPost") int idPost,
            @Body DataToCreateCommentary commentary
    );

    //ROTA DE DELETAR UM COMENTÁRIO
    @DELETE("/posts/commentary/{idPostCommentary}")
    Call<MySqlResult>deletePostCommentary(
            @Header("Authorization") String idToken,
            @Path("idPostCommentary") int idPostCommentary
    );

    /** ROTAS DE ATIVIDADES **/
}