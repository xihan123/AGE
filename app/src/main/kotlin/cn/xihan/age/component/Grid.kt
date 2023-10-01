package cn.xihan.age.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.util.isTablet
import kotlin.math.ceil
import kotlin.math.floor

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/20 21:18
 * @介绍 :
 */
@Composable
fun AnimeGrid(
    modifier: Modifier = Modifier,
    useExpandCardStyle: Boolean = false,
    horizontalSpacing: Dp = 12.dp,
    verticalSpacing: Dp = 5.dp,
    animeList: List<AnimeModel?>,
    onAnimeClick: (Int) -> Unit,
) {
    val isTablet = isTablet()

    val minCardWidth: Dp = if (isTablet) 144.dp else 96.dp

    Layout(modifier = modifier, content = {
        animeList.forEach {
            if (it == null) {
                PlaceholderAnimeCard()
            } else if (useExpandCardStyle) {
                ExpandedAnimeCard(anime = it, onClick = onAnimeClick)
            } else {
                NarrowAnimeCard(anime = it, onClick = onAnimeClick)
            }
        }
    }, measurePolicy = { measures, constraints ->

        var colCnt =
            floor((constraints.maxWidth) / (minCardWidth.toPx() + horizontalSpacing.toPx())).toInt()
                .coerceIn(3, 6)
        if (colCnt == 5) colCnt = 4

        val width = (constraints.maxWidth - (colCnt - 1) * horizontalSpacing.roundToPx()) / colCnt

        val placeable = measures.map { it.measure(constraints.copy(maxWidth = width)) }
        val height = placeable.firstOrNull()?.height ?: 12
        val rowCnt = ceil(placeable.size / colCnt.toFloat()).toInt()

        layout(
            constraints.maxWidth, rowCnt * height + (rowCnt - 1) * verticalSpacing.roundToPx()
        ) {
            placeable.forEachIndexed { index, placeable ->
                val row = index / colCnt
                val col = index % colCnt
                placeable.placeRelative(
                    col * (width + horizontalSpacing.roundToPx()),
                    row * (height + verticalSpacing.roundToPx())
                )
            }
        }
    })
}