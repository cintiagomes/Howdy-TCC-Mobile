package com.example.howdy.remote

import com.example.howdy.model.ActivityTypes.OneActivityInPublicList
import com.example.howdy.model.Friendship
import com.example.howdy.model.MySqlResult
import okhttp3.MultipartBody
import com.example.howdy.model.PostTypes.Post
import okhttp3.RequestBody
import com.example.howdy.model.PostTypes.PostCategory
import com.example.howdy.model.TraductionTypes.DataToTranslate
import com.example.howdy.model.PostTypes.PostCommentaryTypes.Commentary
import com.example.howdy.model.PostTypes.PostCommentaryTypes.DataToCreateCommentary
import com.example.howdy.model.PostTypes.PostWithoutCreator
import com.example.howdy.model.UserTypes.*
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
    //O RETROFIT COLOCA ASPAS EM TODOS OS CAMPOS, ENTÃO É NECESSÁRIO REMOVER AS ASPAS
    @PUT("/users")
    fun editMyAccount(
        @Header("Authorization") idToken: String,
        @Part profilePhotoFile: MultipartBody.Part?,
        @Part backgroundImageFile: MultipartBody.Part?,
        @Part("idTargetLanguage") idTargetLanguage: RequestBody?,
        @Part("idNativeLanguage") idNativeLanguage: RequestBody?,
        @Part("userName") userName: RequestBody?,
        @Part("birthDate") birthDate: RequestBody?,
        @Part("description") description: RequestBody?
    ): Call<MySqlResult>

    /** ROTAS DE AMIZADE  */
    @GET("/friendships/getAllSomeoneFriends/{idUser}")
    fun getAllSomeoneFriends(
        @Header("Authorization") idToken: String,
        @Path("idUser") idUser: Int
    ): Call<List<Friend>>

    @GET("/friendships/isUserMyFriend/{idUser}")
    fun isUserMyFriend(
        @Header("Authorization") idToken: String,
        @Path("idUser") idUser: Int
    ): Call<Friendship>

    @POST("/friendships/{idUserReceiver}")
    fun createFriendshipRequest(
        @Header("Authorization") idToken: String,
        @Path("idUserReceiver") idUserReceiver: Int
    ): Call<MySqlResult>

    @PUT("/friendships/accept/{idUserReceiver}")
    fun acceptFriendshipRequest(
        @Header("Authorization") idToken: String,
        @Path("idUserReceiver") idUserReceiver: Int
    ): Call<MySqlResult>

    @DELETE("/friendships/{idUser}")
    fun deleteFriendship(
        @Header("Authorization") idToken: String,
        @Path("idUser") idUser: Int
    ): Call<MySqlResult>

    /** ROTA DE RANKING  */
    @GET("/ranking/{rankingType}")
    fun getXpRanking(
        @Header("Authorization") idToken: String,
        @Path("rankingType") rankingType: String,
        @Query("nameFilter") nameFilter: String,
    ): Call<List<UserInRanking>>

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
    //ROTA PARA LISTAR AS ATIVIDADES PÚBLICAS
    @GET("/activities")
    fun getPublicActivities(
        @Header("Authorization") idToken: String,
        @Query("maxPrice") maxPrice: Int,
        @Query("idDifficulty") idDifficulty: Int,
        @Query("orderBy") orderBy: String,
    ): Call<List<OneActivityInPublicList>>

    //ROTA PARA LISTAR AS ATIVIDADES CRIADAS POR UM USUÁRIO
    @GET("/activities?maxPrice=150&idDifficulty=1&orderBy=rating")
    fun getActivitiesCreatedbyUser(
        @Header("Authorization") idToken: String,
        @Query("maxPrice") maxPrice: Int,
        @Query("idDifficulty") idDifficulty: Int,
        @Query("orderBy") orderBy: String,
    ): Call<List<OneActivityInPublicList>>

    //ROTA PARA LISTAR AS ATIVIDADES DESBLOQUEADAS POR UM USUÁRIO
    @GET("/activities?maxPrice=150&idDifficulty=1&orderBy=rating")
    fun getActivitiesUnlockedByUser(
        @Header("Authorization") idToken: String,
        @Query("maxPrice") maxPrice: Int,
        @Query("idDifficulty") idDifficulty: Int,
        @Query("orderBy") orderBy: String,
    ): Call<List<OneActivityInPublicList>>
}