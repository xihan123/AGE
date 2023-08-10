package cn.xihan.age.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/12/31 15:26
 * @介绍 :
 */
private suspend fun infinityTextChanged(
    texts: List<String>,
    delayTime: Long = 3000L,
    onTextChange: (String) -> Unit,
): String {
    var item = 0

    while (true) {
        delay(delayTime)

        item = if (item == texts.size - 1) {
            0
        } else {
            item + 1
        }
        onTextChange.invoke(texts[item])
    }
}

@Composable
fun InfinityText(
    modifier: Modifier = Modifier,
    texts: List<String>,
    delayTime: Long = 3000L,
    content: @Composable (targetState: String) -> Unit = {},
) {
    if (texts.isNotEmpty()) {
        var text by rememberSaveable {
            mutableStateOf(texts.first())
        }

        LaunchedEffect(texts) {
            infinityTextChanged(texts, delayTime) {
                text = it
            }
        }

        AnimatedContent(
            modifier = modifier,
            targetState = text,
            transitionSpec = {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
            },
            label = "",
        ) { targetState ->
            content.invoke(targetState)
        }
    }
}

@Composable
fun SearchByTextAppBar(
    modifier: Modifier = Modifier,
    placeholder: Set<String> = emptySet(),
    text: String = "",
    onTextChange: (String) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickSearch: () -> Unit = {},
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp),
        value = text,
        onValueChange = onTextChange,
        singleLine = true,
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            IconButton(onClick = onClickBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        trailingIcon = {
            IconButton(onClick = onClickSearch) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
        placeholder = {
            InfinityText(
                texts = placeholder.toList(),
                delayTime = 4000L,
                content = { targetState ->
                    Text(
                        text = targetState,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ListHistorySearch(
    modifier: Modifier = Modifier,
    listHistory: Set<String> = emptySet(),
    onClickHistoryItem: (String) -> Unit = {}
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 10.dp)
    ) {
        listHistory.forEach {
            ElevatedAssistChip(
                onClick = {
                    onClickHistoryItem(it)
                },
                label = {
                    Text(text = it)
                },
            )
        }
    }

}

@Composable
fun EmptyList(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier.height(60.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
        )
    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier,
    textTitle: String,
    textAction: String = "",
    onClickAction: () -> Unit = {},
) {
    Row(
        modifier = modifier.height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = textTitle,
            style = MaterialTheme.typography.titleLarge,
        )

        if (textAction.isNotBlank()) {
            TextButton(
                onClick = onClickAction
            ) {
                Text(text = textAction)
            }
        }
    }
}