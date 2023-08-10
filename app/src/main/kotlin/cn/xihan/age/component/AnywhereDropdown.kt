package cn.xihan.age.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import cn.xihan.age.util.rememberMutableStateOf

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/28 19:24
 * @介绍 :
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnywhereDropdown(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    surface: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val indication = LocalIndication.current
    val interactionSource = remember { MutableInteractionSource() }
    val state by interactionSource.interactions.collectAsState(null)
    var offset by rememberMutableStateOf(Offset.Zero)
    val dpOffset = with(LocalDensity.current) {
        DpOffset(offset.x.toDp(), offset.y.toDp())
    }

    LaunchedEffect(state) {
        if (state is PressInteraction.Press) {
            val i = state as PressInteraction.Press
            offset = i.pressPosition
        }
        if (state is PressInteraction.Release) {
            val i = state as PressInteraction.Release
            offset = i.press.pressPosition
        }
    }

    Box(
        modifier = modifier
            .combinedClickable(
                interactionSource = interactionSource,
                indication = indication,
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        surface()
        Box {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                offset = dpOffset,
                content = content
            )
        }
    }
}

@Composable
fun MyDropdownMenuItem(
    topAppBarExpanded: MutableState<Boolean>,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    DropdownMenuItem(
        text = text,
        onClick = {
            topAppBarExpanded.value = false
            onClick()
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}