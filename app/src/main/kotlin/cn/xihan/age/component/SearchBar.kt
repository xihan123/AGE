package cn.xihan.age.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.neutral02
import cn.xihan.age.ui.theme.neutral06
import cn.xihan.age.ui.theme.neutral10
import cn.xihan.age.ui.theme.pink50
import cn.xihan.age.ui.theme.pink95
import cn.xihan.age.util.rememberMutableInteractionSource


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/20 14:10
 * @介绍 :
 */
@Composable
fun AgeAnimeSearchBar(
    modifier: Modifier,
    text: String,
    searching: Boolean,
    focusRequester: FocusRequester,
    searchBarState: SearchBarState,
    leftIconId: Int = AgeAnimeIcons.history,
    rightIconId: Int = AgeAnimeIcons.category,
    onLeftIconClick: () -> Unit = {},
    onRightIconClick: () -> Unit = {},
    onFocusChange: (Boolean) -> Unit,
    onInputChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    val interactionResource = rememberMutableInteractionSource()

    Surface(
        modifier = modifier,
        color = if (searching) MaterialTheme.colorScheme.surface else lerp(
            start = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
            stop = MaterialTheme.colorScheme.surface,
            fraction = searchBarState.scrollProgress
        )
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                .statusBarsPadding()
                .padding(15.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            LeftAnimatedIcon(
                isActive = searching,
                iconId = leftIconId,
                iconColor = if (searching) MaterialTheme.colorScheme.onSurface else lerp(
                    start = MaterialTheme.colorScheme.onSurface,
                    stop = MaterialTheme.colorScheme.onSurface,
                    fraction = searchBarState.scrollProgress
                ),
                interactionResource = interactionResource,
                onExitSearching = { onFocusChange(false); },
                onIconClick = onLeftIconClick
            )
            SearchBox(
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { if (it.hasFocus) onFocusChange(true) },
                text = text,
                color = if (searching) MaterialTheme.colorScheme.background else lerp(
                    start = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                    stop = MaterialTheme.colorScheme.surface,
                    fraction = searchBarState.scrollProgress
                ),
                focusRequester = focusRequester,
                onInputChange = { onInputChange(it) },
                onSearch = onSearch
            )
            RightAnimatedIcon(
                isActive = searching,
                iconId = rightIconId,
                interactionResource = interactionResource,
                onIconClick = onRightIconClick,
                onSearch = onSearch,
            )

        }
    }
}

@Composable
private fun SearchBox(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    focusRequester: FocusRequester,
    onInputChange: (String) -> Unit,
    onSearch: () -> Unit,
) {

    val textEntered by remember(text) { derivedStateOf { text.isNotEmpty() } }
    BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = text,
        onValueChange = { onInputChange(it) },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        singleLine = true,
        cursorBrush = SolidColor(pink50),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    ) { innerTextField ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(color)
                .height(30.dp)
                .padding(start = 4.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(AgeAnimeIcons.search),
                contentDescription = null,
            )
            Box(Modifier.weight(1f)) { innerTextField() }
            ClearIcon(
                modifier = Modifier
                    .size(15.dp),
                visible = textEntered,
                interactionResource = rememberMutableInteractionSource(),
                onClearText = {
                    onInputChange("")
                    focusRequester.requestFocus()
                }
            )
        }
    }
}

@Composable
private fun ClearIcon(
    modifier: Modifier = Modifier,
    visible: Boolean,
    interactionResource: MutableInteractionSource,
    onClearText: () -> Unit
) {
    if (visible) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(neutral02)
                .clickable(
                    interactionSource = interactionResource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClearText
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(0.75f),
                painter = painterResource(AgeAnimeIcons.close),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun LeftAnimatedIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    iconId: Int,
    iconColor: Color,
    interactionResource: MutableInteractionSource,
    onExitSearching: () -> Unit,
    onIconClick: () -> Unit,
) {
    AnimatedContent(
        targetState = isActive,
        transitionSpec = {
            (fadeIn() + slideInHorizontally()).togetherWith(
                fadeOut() + slideOutHorizontally() + scaleOut(
                    transformOrigin = TransformOrigin(0f, .5f)
                )
            )
        },
        contentAlignment = Alignment.Center, label = ""
    ) { activated ->
        if (activated) {
            // back icon
            Icon(
                modifier = modifier.clickable(
                    interactionSource = interactionResource,
                    indication = null,
                    role = Role.Button,
                    onClick = onExitSearching
                ),
                painter = painterResource(AgeAnimeIcons.arrowLeft),
                contentDescription = null,
                tint = iconColor
            )
        } else {
            // action icon
            Icon(
                modifier = modifier.clickable(
                    interactionSource = interactionResource,
                    indication = null,
                    role = Role.Button,
                    onClick = onIconClick
                ),
                painter = painterResource(iconId),
                contentDescription = null,
                tint = iconColor
            )
        }
    }
}

@Composable
private fun RightAnimatedIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    iconId: Int,
    interactionResource: MutableInteractionSource,
    onSearch: () -> Unit,
    onIconClick: () -> Unit,
) {
    AnimatedContent(
        targetState = isActive,
        transitionSpec = {
            (fadeIn() + slideInHorizontally { it / 2 }).togetherWith(fadeOut() + slideOutHorizontally { it / 2 } + scaleOut(
                transformOrigin = TransformOrigin(1f, .5f)
            ))
        },
        contentAlignment = Alignment.Center, label = ""
    ) { activated ->
        if (activated) {
            // search icon
            Text(
                modifier = modifier.clickable(
                    interactionSource = interactionResource,
                    indication = null,
                    role = Role.Button,
                    onClick = onSearch
                ),
                text = "搜索",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            // action icon
            Icon(
                modifier = modifier.clickable(
                    interactionSource = interactionResource,
                    indication = null,
                    role = Role.Button,
                    onClick = onIconClick
                ),
                painter = painterResource(iconId),
                contentDescription = null
            )
        }
    }
}

@Composable
fun rememberSearchBarState(
    searching: Boolean,
    focusRequester: FocusRequester,
    scrollProgress: Float
): SearchBarState {
    return remember(scrollProgress, focusRequester, searching) {
        SearchBarState(
            searching,
            focusRequester,
            scrollProgress
        )
    }
}

@Stable
class SearchBarState(
    var searching: Boolean,
    val focusRequester: FocusRequester,
    val scrollProgress: Float,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SearchHistoryView(
    searchHistory: Set<String>,
    onClearHistory: () -> Unit,
    onRecordClick: (String) -> Unit,
) {

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp, 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (searchHistory.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    text = "搜索历史",
                    textAlign = TextAlign.Start,
                    color = neutral10,
                    style = MaterialTheme.typography.bodyMedium
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (record in searchHistory.reversed()) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clip(CircleShape)
                                .background(pink95.copy(alpha = 0.6f))
                                .padding(10.dp, 6.dp)
                                .clickable(
                                    interactionSource = rememberMutableInteractionSource(),
                                    indication = null,
                                    role = Role.Button,
                                    onClick = { onRecordClick(record) }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = record, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(
                            interactionSource = rememberMutableInteractionSource(),
                            indication = null,
                            role = Role.Button,
                            onClick = onClearHistory,
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(AgeAnimeIcons.trash),
                        contentDescription = null,
                        tint = neutral06
                    )
                    Text(
                        text = "清空搜索历史",
                        color = neutral06,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
