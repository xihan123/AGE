package cn.xihan.age.model

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/12/31 21:22
 * @介绍 :
 */
data class AlertDialogModel(
    val title: String,
    val message: String,
    val positiveMessage: String? = null,
    val positiveObject: Any? = null,
    val negativeMessage: String? = null,
    val negativeObject: Any? = null,
)
