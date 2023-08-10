package cn.xihan.age.component


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.xihan.age.R
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.AlertDialog
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logDebug

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/26 13:19
 * @介绍 :
 */
@Composable
fun <VS : IUiState> HandleError(
    state: VS,
    modifier: Modifier = Modifier,
    onShowSnackBar: (message: String) -> Unit = {},
    onPositiveAction: (value: Any?) -> Unit = { _ -> },
    onNegativeAction: (value: Any?) -> Unit = { _ -> },
    onDismissErrorDialog: () -> Unit = {},
    content: @Composable (VS) -> Unit,
) {
    Box(modifier = modifier) {
        content(state)

        if (state.loading) {
            FullScreenLoading()
        }

        if (state.error != null) {
            HandleError(
                error = state.error!!, // Not null
                onPositiveAction = onPositiveAction,
                onNegativeAction = onNegativeAction,
                onShowSnackBar = onShowSnackBar,
                onDismissRequest = onDismissErrorDialog,
            )
        }


    }

}

@Composable
fun FullScreenLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {},
                ),
        ), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.background(Color.Transparent),
            strokeWidth = 5.dp,
        )
    }
}

@Composable
fun HandleError(
    error: AgeException,
    onPositiveAction: (value: Any?) -> Unit = { _ -> },
    onNegativeAction: (value: Any?) -> Unit = { _ -> },
    onShowSnackBar: (message: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val context = LocalContext.current

    when (error) {
        is AgeException.AlertException -> {
            AlertDialog(
                alertDialog = error.alertDialog,
                onDismissRequest = onDismissRequest,
                positiveAction = onPositiveAction,
                negativeAction = onNegativeAction,
            )
        }
        is AgeException.InlineException -> {
            ErrorItem(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                errorMessage = stringResource(id = R.string.error_empty),
                onRetryClick = {
                    onPositiveAction.invoke(null)
                }
            )
        }

        else -> {
            LaunchedEffect(key1 = true) {
                onShowSnackBar.invoke(error.message ?: context.getString(R.string.error_params))
                onDismissRequest.invoke()
            }
        }
    }
}

@Composable
fun AlertDialog(
    alertDialog: AlertDialog,
    positiveAction: (value: Any?) -> Unit = { _ -> },
    negativeAction: (value: Any?) -> Unit = { _ -> },
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = alertDialog.title)
        },
        text = {
            Text(text = alertDialog.message)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                    positiveAction.invoke(alertDialog.positiveObject)
                },
            ) {
                Text(
                    text = alertDialog.positiveMessage
                        ?: stringResource(id = android.R.string.ok)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                    negativeAction.invoke(alertDialog.positiveObject)
                },
            ) {
                Text(
                    text = alertDialog.negativeMessage
                        ?: stringResource(id = android.R.string.cancel)
                )
            }
        },
    )
}
