package cn.xihan.age.util

import org.jsoup.Jsoup

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/31 21:43
 * @介绍 :
 */
object JsoupUtil {

    /**
     * 解析番剧播放地址
     */
    fun parseAnimeUrl(html: String): String = runCatching {
        val doc = Jsoup.parse(html)
        val script = doc.select("body > script")
        val text = script.html()
        // 从 script 标签中提取 video_url = 后面 '' 内 数据
        text.substringAfter("video_url = '").substringBefore("';")
    }.getOrElse {
        ""
    }

}