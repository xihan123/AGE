package cn.xihan.age.util

import androidx.appcompat.app.AppCompatDelegate

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/18 23:54
 * @介绍 :
 */
object Settings : IMMKVOwner by MMKVOwner(mmapID = "settings") {

    var autoCheckUpdate by mmkvBool(default = false)

    var autoFullscreen by mmkvBool(default = true)
    var playSkipTime by mmkvInt(default = 30)
    var playAspectRatio by mmkvInt(default = 3)
    var playSpeed by mmkvInt(default = 1)

    var scheduleFilterType by mmkvString(default = "全部")

    var themeMode by mmkvInt(default = AppCompatDelegate.MODE_NIGHT_YES)

    var hideUserName by mmkvBool(default = false)

}