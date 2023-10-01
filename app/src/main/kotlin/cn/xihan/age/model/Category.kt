package cn.xihan.age.model

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 0:37
 * @介绍 :
 */
enum class Category(
    val key: String,
    val title: String,
    val options: List<Pair<String, String>>,
) {
    Genre(
        key = "genre",
        title = "版本",
        options = listOf("all" to "全部", "TV" to "TV", "剧场版" to "剧场版", "OVA" to "OVA"),
    ),
    Label(
        key = "label",
        title = "风格",
        options = listOf(
            "all" to "全部",
            "搞笑" to "搞笑", "运动" to "运动", "励志" to "励志", "热血" to "热血",
            "战斗" to "战斗", "竞技" to "竞技", "校园" to "校园", "青春" to "青春",
            "爱情" to "爱情", "冒险" to "冒险", "后宫" to "后宫", "百合" to "百合",
            "治愈" to "治愈", "萝莉" to "萝莉", "魔法" to "魔法", "悬疑" to "悬疑",
            "推理" to "推理", "奇幻" to "奇幻", "科幻" to "科幻", "游戏" to "游戏",
            "神魔" to "神魔", "恐怖" to "恐怖", "血腥" to "血腥", "机战" to "机战",
            "战争" to "战争", "犯罪" to "犯罪", "历史" to "历史", "社会" to "社会",
            "职场" to "职场", "剧情" to "剧情", "伪娘" to "伪娘", "耽美" to "耽美",
            "童年" to "童年", "教育" to "教育", "亲子" to "亲子", "真人" to "真人",
            "歌舞" to "歌舞", "肉番" to "肉番", "美少女" to "美少女", "轻小说" to "轻小说",
            "吸血鬼" to "吸血鬼", "女性向" to "女性向", "泡面番" to "泡面番", "欢乐向" to "欢乐向"
        )
    ),
    Letter(
        key = "letter",
        title = "首字母",
        options = listOf(
            "all" to "全部",
            ("A" to "A"), ("B" to "B"), ("C" to "C"), ("D" to "D"), ("E" to "E"),
            ("F" to "F"), ("G" to "G"), ("H" to "H"), ("I" to "I"), ("J" to "J"),
            ("K" to "K"), ("L" to "L"), ("M" to "M"), ("N" to "N"), ("O" to "O"),
            ("P" to "P"), ("Q" to "Q"), ("R" to "R"), ("S" to "S"), ("T" to "T"),
            ("U" to "U"), ("V" to "V"), ("W" to "W"), ("X" to "X"), ("Y" to "Y"),
            ("Z" to "Z")
        )
    ),
    Order(
        key = "order",
        title = "排序",
        options = listOf("time" to "时间排序", "点击量" to "点击量", "名称" to "名称"),
    ),
    Region(
        key = "region",
        title = "地区",
        options = listOf("all" to "全部", "日本" to "日本", "中国" to "中国", "欧美" to "欧美"),
    ),
    Resource(
        key = "resource",
        title = "资源",
        options = listOf("all" to "全部", "BDRIP" to "BDRIP", "AGE-RIP" to "AGE-RIP"),
    ),
    Season(
        key = "season",
        title = "季度",
        options = listOf("all" to "全部", "1" to "1月", "4" to "4月", "7" to "7月", "10" to "10月"),
    ),
    Status(
        key = "status",
        title = "状态",
        options = listOf("all" to "全部", "连载" to "连载", "完结" to "完结", "未播放" to "未播放"),
    ),
    Year(
        key = "year",
        title = "年份",
        options = buildList {
            add("all" to "全部")
            for (year in java.time.LocalDate.now().year downTo 2001) {
                add(year.toString() to year.toString())
            }
            add("2000以前" to "2000以前")
        },
    )
}

fun Category.defaultLabel() = options.first().second

fun Category.labelValueOf(option: String): String =
    when (this) {
        Category.Label -> if (option == "") "全部" else option
        else -> options.find { it.first == option }?.second ?: "全部"
    }
