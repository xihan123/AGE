package cn.xihan.age.network

import android.content.Context
import android.content.SharedPreferences
import cn.xihan.age.BuildConfig
import cn.xihan.age.model.MainConfigModel
import cn.xihan.age.util.extension.MMKVOwner
import cn.xihan.age.util.extension.PreferenceOwner
import cn.xihan.age.util.extension.application
import cn.xihan.age.util.extension.mmkvParcelable
import cn.xihan.age.util.extension.preferenceBool
import cn.xihan.age.util.extension.preferenceInt
import cn.xihan.age.util.extension.preferenceString


object Api {

    val BASE_URL =  //"https://app.age-api.com:8443/v2/"
        Settings.mainConfigModel.aGEEntity.coreApi.ifBlank { "https://app.age-api.com:8443/v2/" }
    val PHONE_DETAIL_URL = //"https://web.age-spa.com:8443/#/detail/"
        Settings.mainConfigModel.aGEEntity.mobileWebsiteLink.ifBlank { "https://web.age-spa.com:8443/#/detail/" }

    /**
     * 官网地址
     */
    val WEB_URL =
        Settings.mainConfigModel.aGEEntity.officialWebsiteLink.ifBlank { "https://www.age.tv/" }

    /**
     * 免责声明
     */
    val INTRO_URL =
        Settings.mainConfigModel.appEntity.disclaimerLink.ifBlank { "https://xihan123.github.io/AGE/intro.html" }

    /**
     * X5 debug
     */
    const val X5_DEBUG_URL = "http://debugx5.qq.com"

    /**
     * 统一时间格式
     */
    val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    /**
     * Github地址
     */
    const val GITHUB_URL = "https://github.com/xihan123/AGE"

    const val SYNC_INFO_URL =
        "https://github.com/xihan123/AGE-API/raw/master/details/%E8%AE%B0%E5%BD%95.txt"

    const val KTOR_AGE_EXTENSIONS = "https://github.com/xihan123/ktor-age-extensions"

    const val DEFAULT_1K_SPLASH_PIC =
        "https://pic1.imgdb.cn/item/6353a93c16f2c2beb194a2cc.jpg"
    const val DEFAULT_2K_SPLASH_PIC =
        "https://pic1.imgdb.cn/item/6353ac3116f2c2beb1991ebd.jpg"

    /**
     * 百度解析
     */
    val BAIDU_ANALYTICS_ID =
        "https://bos.nj.bpc.baidu.com/tieba-smallvideo/%s?t=${System.currentTimeMillis()}"

    const val INTENT_URL_FORMAT =
        "intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{payCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end"

    const val CONFIG_URL =
        "https://jsd.cdn.zzko.cn/gh/xihan123/AGE-API@master/details/Main-740.json"
//    "http://192.168.43.110/AGE-API/details/Main-740.json"
    //"https://age-api.mengzx.cn:8443/Main-740.json"
//    val CONFIG_URL_ARRAY = arrayOf(
//        "https://age-api-test.xihan.asia:8443/Main-740.json",
//        "https://raw.fastgit.ixmu.net/xihan123/AGE-API/master/details/Main-740.json",
//        "https://jsd.cdn.zzko.cn/gh/xihan123/AGE-API@master/details/Main-740.json",
//        "https://cdn.staticaly.com/gh/xihan123/AGE-API/master/details/Main-740.json",
//        "https://fastly.jsdelivr.net/gh/xihan123/AGE-API@master/details/Main-740.json",
//        "https://cdn.jsdelivr.net/gh/xihan123/AGE-API@master/details/Main-740.json"
//    )

}

object SPSettings : PreferenceOwner {

    override val prefs: SharedPreferences by lazy {
        application.applicationContext.getSharedPreferences(
            "${BuildConfig.APPLICATION_ID}_preferences",
            Context.MODE_PRIVATE
        )
    }

    var isTheFirstTime by preferenceBool(default = true)

    /**
     * 主题模式 0:浅色 1:深色 2:跟随系统
     */
    var themeMode by preferenceInt(default = 1)

    /**
     * 播放跳进度
     */
    var playSkipTime by preferenceInt(default = 15)

    /**
     * 播放速度
     */
    var playSpeed by preferenceInt(default = 1)

    /**
     * 播放嗅探超时时间
     */
    var playSniffingTimeout by preferenceInt(default = 30)

    /**
     * 是隐藏底部进度条
     */
    var hideBottomProgress by preferenceBool(default = false)

    /**
     * 是自动下一集
     */
    var autoNextEpisode by preferenceBool(default = false)

    /**
     * 画面比例
     */
    var playAspectRatio by preferenceInt(default = 3)

    /**
     * 启用自定义API
     */
    var customAPI by preferenceBool(default = false)

    /**
     * 自定义API
     */
    var customApiIndex by preferenceInt(default = 0)

    /**
     * 自定义API名称
     */
    var customApiName by preferenceString(default = "官方API")

    var customApiUrl by preferenceString(default = "https://jsd.cdn.zzko.cn/gh/xihan123/AGE-API@master/details/")

    /**
     * 启用安全DNS
     */
    var safeDns by preferenceBool(default = false)

    /**
     * 安全DNS ip地址
     */
    var safeDnsIp by preferenceString(default = "210.2.4.8")

    /**
     * 是否启用x5内核
     */
    var enableX5 by preferenceBool(default = false)

    /**
     * x5内核是否可用
     */
    var x5Available by preferenceBool(default = false)

}

object Settings : MMKVOwner("Settings") {

    var mainConfigModel by mmkvParcelable(default = MainConfigModel())

}


