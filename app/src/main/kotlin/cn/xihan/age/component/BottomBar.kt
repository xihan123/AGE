package cn.xihan.age.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import cn.xihan.age.ui.theme.darkPink30
import cn.xihan.age.ui.theme.pink50
import cn.xihan.age.util.rememberMutableInteractionSource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AgeAnimeBottomAppBar(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    onNavigateTo: (TopLevelScreen) -> Unit,
) {
    Surface(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .alpha(0.95f)
            .fillMaxWidth()
            .selectableGroup(), tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            TopLevelScreen.entries.forEach { destination ->
                BottomAppBarItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    selected = destination.route == currentDestination?.route,
                    onClick = { onNavigateTo(destination) },
                    iconId = destination.iconId,
                    label = destination.label,
                )
            }
        }
    }
}

@Composable
fun BottomAppBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    iconId: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val animatedIcon by rememberLottieComposition(LottieCompositionSpec.RawRes(iconId))
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (selected) 1f else 0f, animationSpec = tween(
            800, easing = LinearEasing
        ), label = ""
    )

    Box(
        modifier.selectable(
            selected = selected,
            onClick = onClick,
            role = Role.Tab,
            interactionSource = rememberMutableInteractionSource(),
            indication = null,
        ), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieAnimation(animatedIcon, progress = {
                if (selected) animationProgress else 0f
            })
            Text(
                text = label,
                fontSize = TextUnit(9f, TextUnitType.Sp),
                color = if (selected) pink50 else darkPink30
            )
        }
    }
}