package cn.xihan.age.util


import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.model.CommentResponseModel
import cn.xihan.age.model.GeneralizedResponseModel
import cn.xihan.age.model.HomeModel
import cn.xihan.age.model.LoginResponseModel
import cn.xihan.age.model.RankingModel
import cn.xihan.age.model.SearchModel
import cn.xihan.age.model.SpacaptchaModel
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HEAD
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/17 21:13
 * @介绍 :
 */
object Api {

    const val API_BASE_URL_V1 = "https://api.agedm.vip/v2/"
    const val API_BASE_URL_V2 = "https://api.agemys.org/v2/"
    const val JSOUP_BASE_URL = "https://www.agemys.org/"
    const val RELEASE_LATEST = "https://api.github.com/repos/xihan123/AGE/releases/latest"
    const val AGE_GITHUB = "https://github.com/xihan123/AGE"

}

interface RemoteService {

    @GET
    suspend fun getResponseBody(@Url url: String): String

    /**
     * 获取主页模型
     */
    @GET("home-list")
    fun getHomeModel(): Flow<HomeModel>

    /**
     * 获取轮播图模型
     */
    @GET("slipic")
    fun getBannerModel(): Flow<String>

    @GET("search")
    suspend fun getSearchModel(
        @Query("query") query: String,
        @Query("page") page: Int,
    ): SearchModel

    /**
     * 获取番剧信息模型
     * @param aid 番剧id
     */
    @GET("detail/{aid}")
    fun getAnimeDetailModel(@Path("aid") aid: Int): Flow<AnimeDetailModel>

    /**
     * 获取分类模型
     * @param type 分类类型
     * @param page 页码
     * @param size 每页个数
     */
    @GET("catalog")
    suspend fun getCategoryModel(
        @QueryMap type: Map<String, String>, @Query("page") page: Int, @Query("size") size: Int
    ): CatalogModel

    @HEAD
    suspend fun getHeader(@Url url: String): HttpResponse

    @GET("update")
    suspend fun getRecentUpdates(
        @Query("page") page: Int, @Query("size") size: Int
    ): GeneralizedResponseModel

    @GET("recommend")
    fun getRecommend(): Flow<GeneralizedResponseModel>

    /**
     * 获取排行榜
     */
    @GET("rank")
    fun getRankModel(
        @Query("year") year: String
    ): Flow<RankingModel>

    @GET("spacaptcha")
    fun getSpacaptcha(): Flow<SpacaptchaModel>

    @POST("account/login/")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("captcha") captcha: String,
        @Field("key") key: String,
        @Field("action") action: String = "login"
    ): Flow<LoginResponseModel>

    @GET("my/collects")
    fun getCollects(
        @Query("username") username: String,
        @Query("sign_t") signT: Int,
        @Query("sign_k") signK: Int,
    ): Flow<GeneralizedResponseModel>

}

interface JsoupService {

    @GET("play/{animeId}/{playIndex}/{episodeIndex}")
    suspend fun getAnimeUrl(
        @Path("animeId") animeId: Int,
        @Path("playIndex") playIndex: Int,
        @Path("episodeIndex") episodeIndex: Int
    ): String

    /**
     * 获取番剧评论
     * @param [aid] 番剧id
     * @param [page] 页码
     */
    @GET("comment/{aid}/")
    suspend fun getCommentModel(
        @Path("aid") aid: Int,
        @Query("page") page: Int
    ): CommentResponseModel

    /**
     * 获取一周更新
     */
    @GET("update")
    suspend fun getUpdate(): String
}
