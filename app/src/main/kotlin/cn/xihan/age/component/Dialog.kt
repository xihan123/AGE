package cn.xihan.age.component

import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import cn.xihan.age.R
import cn.xihan.age.ui.theme.pink
import cn.xihan.age.ui.theme.pink40
import cn.xihan.age.ui.theme.pink95
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.rememberMutableInteractionSource

@Composable
fun WorkingInProgressDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("了解了", color = pink40)
            }
        },
        onDismissRequest = onDismiss,
        icon = {
            Image(
                painter = painterResource(R.drawable.working_in_progress),
                contentDescription = "working in progress"
            )
        },
        title = {
            Text(
                text = "此项功能正在开发中",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        containerColor = pink95,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDialog(
    versionName: String,
    updateNotes: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isTablet = isTablet()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .requiredHeightIn(
                max = min(
                    if (isTablet) 540.dp else 360.dp,
                    LocalConfiguration.current.screenHeightDp.dp * 4 / 5
                )
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(R.drawable.update_popover_header_bg),
                    contentDescription = "header"
                )
                Column(
                    modifier = Modifier.padding(start = 18.dp, top = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "发现新版本",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "v$versionName",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            val hPadding = with(LocalDensity.current) { 15.dp.roundToPx() }
            val vPadding = with(LocalDensity.current) { 12.dp.roundToPx() }
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                factory = { context ->
                    TextView(context).apply {
                        setLineSpacing(20f, 1f)
                        setPadding(hPadding, vPadding, hPadding, 0)
                    }
                },
                update = {
                    it.text = HtmlCompat.fromHtml(
                        updateNotes ?: "优化应用体验，修复若干问题",
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                }
            )
            FilledTonalButton(
                modifier = Modifier.padding(top = 12.dp),
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = pink40
                ),
                contentPadding = PaddingValues(64.dp, 6.dp)
            ) {
                Text(text = "立即升级", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier
            .height(96.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 12.dp),
        onDismissRequest = onDismiss,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Text(
                    modifier = Modifier.clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Button,
                        onClick = onDismiss
                    ),
                    text = "取消",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    modifier = Modifier.clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Button,
                        onClick = onConfirm
                    ),
                    text = "确定",
                    color = pink,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}