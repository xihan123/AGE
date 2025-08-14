package cn.xihan.age.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cn.xihan.age.ui.theme.AgeAnimeIcons
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun AnimatedFollowIcon(
    modifier: Modifier = Modifier, isFollowed: Boolean
) {
    val followIcon by rememberLottieComposition(
        LottieCompositionSpec.RawRes(AgeAnimeIcons.Animated.follow)
    )
    val animationProgress by animateFloatAsState(
        targetValue = if (isFollowed) 1f else 0f, animationSpec = tween(
            800, easing = LinearEasing
        ), label = ""
    )

    LottieAnimation(
        modifier = modifier,
        composition = followIcon,
        progress = { if (isFollowed) animationProgress else 0f })
}

@Composable
fun AnimatedRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    durationMillis: Int = 600,
) {
    val radioButton by rememberLottieComposition(
        LottieCompositionSpec.RawRes(AgeAnimeIcons.Animated.radio)
    )

    val selectProgress by animateFloatAsState(
        targetValue = if (selected) 0.5f else 0f,
        animationSpec = tween(durationMillis, easing = LinearEasing),
        label = ""
    )

    LottieAnimation(
        modifier = modifier,
        composition = radioButton,
        progress = { if (selected) selectProgress else 0f })
}

@Composable
fun AnimatedSwitchButton(
    modifier: Modifier = Modifier,
    checked: Boolean?,
) {
    val switchButton by rememberLottieComposition(
        LottieCompositionSpec.RawRes(AgeAnimeIcons.Animated.switch)
    )

    if (checked == null) {
        LottieAnimation(modifier = modifier, composition = switchButton, progress = { 1f })
    } else {
        val animationProgress by animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = tween(400, easing = LinearEasing),
            label = ""
        )

        LottieAnimation(
            modifier = modifier,
            composition = switchButton,
            progress = { animationProgress })
    }
}