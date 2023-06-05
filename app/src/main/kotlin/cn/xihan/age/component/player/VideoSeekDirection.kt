package cn.xihan.age.component.player

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/30 9:52
 * @介绍 :
 */
enum class VideoSeekDirection {
    NONE,
    Rewind,
    Forward;

    val isSeeking: Boolean
        get() = this != NONE
}