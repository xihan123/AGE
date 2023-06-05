package cn.xihan.age.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.xihan.age.base.IUiState


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/26 15:50
 * @介绍 :
 */
@Composable
fun <VS : IUiState>AgeScaffold(
    state: VS,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    onShowSnackbar: (message: String) -> Unit = {},
    onErrorPositiveAction: (value: Any?) -> Unit = { _ -> },
    onErrorNegativeAction: (value: Any?) -> Unit = { _ -> },
    onDismissErrorDialog: () -> Unit = {},
    content: @Composable (PaddingValues, VS) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
    ) { paddingValues ->
        HandleError(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = state,
            onShowSnackBar = onShowSnackbar,
            onPositiveAction = onErrorPositiveAction,
            onNegativeAction = onErrorNegativeAction,
            onDismissErrorDialog = onDismissErrorDialog,
        ) { viewState ->
            content.invoke(paddingValues, viewState)
        }
    }
}