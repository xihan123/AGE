package cn.xihan.age.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.pink95
import cn.xihan.age.util.rememberMutableInteractionSource

@Composable
fun SolidTopBar(
    title: String,
    leftIconId: Int = AgeAnimeIcons.arrowLeft,
    onLeftIconClick: () -> Unit = {},
    rightIconId: Int? = null,
    onRightIconClick: (() -> Unit) = {},
) {

    val interactionResource = rememberMutableInteractionSource()

    Surface {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                .statusBarsPadding()
                .padding(15.dp, 12.dp),
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable(
                        interactionSource = interactionResource,
                        indication = null,
                        role = Role.Button,
                        onClick = onLeftIconClick
                    ),
                painter = painterResource(leftIconId),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            if (rightIconId != null) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(
                            interactionSource = interactionResource,
                            indication = null,
                            role = Role.Button,
                            onClick = onRightIconClick
                        ),
                    painter = painterResource(rightIconId),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun TransparentTopBar(
    title: String,
    iconId: Int,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onIconClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(20.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
//            fontFamily = AgeAnimeFontFamilies.heiFontFamily,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.titleLarge
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(pink95)
                .clickable(role = Role.Button, onClick = onIconClick)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                tint = iconTint
            )
        }
    }
}
