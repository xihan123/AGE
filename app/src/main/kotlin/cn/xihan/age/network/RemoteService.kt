package cn.xihan.age.network


import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.model.HomeListModel
import cn.xihan.age.model.MainConfigModel
import cn.xihan.age.model.RankModel
import cn.xihan.age.model.RecommendModel
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Url
import kotlinx.coroutines.flow.Flow


interface RemoteService {

    /**
     * 获取配置模型
     * @param url 配置地址
     */
    @GET("")
    fun getConfigModel(
        @Url url: String
    ): Flow<MainConfigModel>

    /**
     * 获取主页模型
     * @param update 更新个数
     * @param recommend 推荐个数
     */
    @GET("home-list")
    suspend fun getHomeListModel(
        @Query("recommend") recommend: Int, @Query("update") update: Int,
    ): HomeListModel

    /**
     * 获取轮播图模型
     */
    @GET("slipic")
    suspend fun getBannerModel(): String

    /**
     * 获取分类模型
     * @param type 分类类型
     * @param page 页码
     * @param size 每页个数
     */
    @GET("catalog")
    suspend fun getCategoryModel(
        @QueryMap type: Map<String, String>, @Query("page") page: Int, @Query("size") size: Int,
    ): CatalogModel

    /**
     * 获取番剧信息模型
     * @param aid 番剧id
     */
    @GET("detail/{aid}")
    fun getAnimeDetailModel(@Path("aid") aid: String): Flow<AnimeDetailModel>

    /**
     * 获取番剧信息模型
     * @param url 地址
     */
    @GET("")
    fun getCustomAnimeDetailModel(@Url url: String): Flow<AnimeDetailModel>

    /**
     * 获取番剧播放地址
     */
    @GET("")
    suspend fun getAnimePlayUrl(@Url url: String): String

    /**
     * 获取推荐模型
     * @param size 个数
     */
    @GET("recommend")
    fun getRecommendModel(@Query("size") size: Int): Flow<RecommendModel>

    /**
     * 获取排行榜模型
     * @param value 年份
     * @param page 页码
     * @param size 每页个数
     */
    @GET("rank")
    suspend fun getRankModel(
        @Query("value") value: String, @Query("page") page: Int, @Query("size") size: Int,
    ): RankModel

}