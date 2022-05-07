package com.example.howdy.remote

import com.example.howdy.model.UserTypes.DataToCreateUser
import com.example.howdy.model.MySqlResult
import com.example.howdy.model.UserTypes.User
import okhttp3.MultipartBody
import com.example.howdy.model.PostTypes.Post
import okhttp3.RequestBody
import com.example.howdy.model.PostTypes.PostCategory
import com.example.howdy.model.TraductionTypes.DataToTranslate
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.model.PostTypes.PostCommentaryTypes.DataToCreateCommentary
import com.example.howdy.model.PostTypes.PostWithoutCreator
import com.example.howdy.model.UserTypes.UserCollectedWithId
import retrofit2.Call
import retrofit2.http.*

interface RouterInterface {
    /** ROTAS DE USUÁRIO  */ //ROTA PARA CADASTRAR USUÁRIO
    @POST("/users")
    fun createUser(
        @Body user: DataToCreateUser,
        @Header("Authorization") idToken: String
    ): Call<MySqlResult>

    //ROTA PARA CONFERIR SE O USUÁRIO POSSUI UMA RESPECTIVA CONTA NO BANCO SQL
    @GET("/users/isMyUidExternalRegistered")
    fun isMyUidExternalRegistered(@Header("Authorization") idToken: String): Call<List<User>>

    //ROTA PARA COLETAR DADOS DO USUÁRIO CONFORME SEU ID
    @GET("/users/{idUser}")
    fun getUserById(
        @Header("Authorization") idToken: String,
        @Path("idUser") idUser: Int
    ): Call<List<UserCollectedWithId>>

    //ROTA PARA DELETAR CONTA DO USUÁRIO LOGADO
    @DELETE("/users")
    fun deleteMyAccountOnBackend(@Header("Authorization") idToken: String): Call<MySqlResult>

    //ROTA PARA EDITAR CONTA DO USUÁRIO LOGADO
    @Multipart
    @PUT("/users")
    fun editMyAccount(
        @Header("Authorization") idToken: String,
        @Part profilePhotoFile: MultipartBody.Part?,
        @Part backgroundImageFile: MultipartBody.Part?,
        @Path("idTargetLanguage") idTargetLanguage: Int?,
        @Path("idNativeLanguage") idNativeLanguage: Int?,
        @Part("userName") userName: String?,
        @Part("birthDate") birthDate: String?,
        @Part("description") description: String?
    ): Call<MySqlResult>

    /** ROTAS DE POSTAGENS  */
    //ROTA PARA COLETAR POSTAGENS PÚBLICAS, "categoryFilter" pode ser o id de cada categoria, "popular", ou "myFriends"
    @GET("/posts/category/{categoryFilter}")
    fun getPublicPosts(
        @Path("categoryFilter") categoryFilter: String, @Header("Authorization") idToken: String
    ): Call<List<Post>>

    //ROTA PARA COLETAR POSTAGENS CRIADAS POR UM DETERMINADO USUÁRIO
    @GET("/posts/user/{idUser}")
    fun getUserPosts(
        @Path("idUser") idUser: Int, @Header("Authorization") idToken: String
    ): Call<List<PostWithoutCreator>>

    //ROTA PARA CRIAR POSTAGENS
    @Multipart
    @POST("/posts")
    fun createPost(
        @Part("textContent") textContent: RequestBody,
        @Part("isPublic") isPublic: RequestBody,
        @Part("idPostCategory") idPostCategory: RequestBody,
        @Header("Authorization") idToken: String,
        @Part file: MultipartBody.Part?
    ): Call<MySqlResult>

    //ROTA PARA EDITAR UMA POSTAGEM
    @Multipart
    @PUT("/posts/{idPost}")
    fun editPostWithImage(
        @Path("idPost") idPost: Int,
        @Part file: MultipartBody.Part?,
        @Part("textContent") textContent: RequestBody,
        @Part("isPublic") isPublic: RequestBody,
        @Part("idPostCategory") idPostCategory: RequestBody,
        @Header("Authorization") idToken: String
    ): Call<MySqlResult>

    //ROTA PARA DELETAR UMA POSTAGEM
    @DELETE("/posts/{idPost}")
    fun createPostWithImage(
        @Path("idPost") idPost: Int, @Header("Authorization") idToken: String
    ): Call<MySqlResult>

    /** ROTAS DE CURTIDAS DE POSTAGENS  */ //ROTA PARA CURTIR UMA POSTAGEM
    @POST("/posts/like/{idPost}")
    fun likePost(
        @Header("Authorization") idToken: String,
        @Path("idPost") idPost: Int
    ): Call<MySqlResult>

    //ROTA PARA DESCURTIR UMA POSTAGEM
    @DELETE("/posts/like/{idPost}")
    fun unlikePost(
        @Header("Authorization") idToken: String,
        @Path("idPost") idPost: Int
    ): Call<MySqlResult>

    /** ROTAS DE CATEGORIAS DE POSTAGENS  */
    @GET("/postCategories")
    fun getPostCategories(
        @Header("Authorization") idToken: String
    ): Call<List<PostCategory>>

    /** ROTA DE TRADUÇÃO  */
    @POST("/traductions")
    fun traductText(
        @Header("Authorization") idToken: String,
        @Body dataToTranslate: DataToTranslate
    ): Call<List<String>>

    /** ROTAS DE COMENTÁRIOS DE POSTAGENS  */ //ROTA PARA OBTER OS COMENTÁRIOS DE UMA POSTAGEM
    @GET("/posts/commentary/{idPost}")
    fun getPostComments(
        @Header("Authorization") idToken: String,
        @Path("idPost") idPost: Int
    ): Call<List<Commentary>>

    //ROTA PARA EDITAR UM COMENTÁRIO
    @PUT("/posts/commentary/{idPostCommentary}")
    fun editPostCommentary(
        @Header("Authorization") idToken: String,
        @Path("idPostCommentary") idPostCommentary: Int,
        @Body commentary: DataToCreateCommentary
    ): Call<MySqlResult>

    //ROTA PARA CRIAR UM COMENTÁRIO
    @POST("/posts/commentary/{idPost}")
    fun commentPost(
        @Header("Authorization") idToken: String,
        @Path("idPost") idPost: Int,
        @Body commentary: DataToCreateCommentary
    ): Call<MySqlResult>

    //ROTA DE DELETAR UM COMENTÁRIO
    @DELETE("/posts/commentary/{idPostCommentary}")
    fun deletePostCommentary(
        @Header("Authorization") idToken: String,
        @Path("idPostCommentary") idPostCommentary: Int
    ): Call<MySqlResult>
    /** ROTAS DE ATIVIDADES  */
}