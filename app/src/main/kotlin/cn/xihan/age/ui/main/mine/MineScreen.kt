package cn.xihan.age.ui.main.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimatedSwitchButton
import cn.xihan.age.model.AlertDialogModel
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.Api
import cn.xihan.age.util.Settings
import cn.xihan.age.util.getAspectRadio
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.openUrl
import cn.xihan.age.util.rememberMutableInteractionSource
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.util.rememberSavableMutableStateOf
import cn.xihan.age.util.toBitmap
import coil.compose.AsyncImage
import org.orbitmvi.orbit.compose.collectAsState
import java.time.LocalTime

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 14:14
 * @介绍 :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(
    padding: PaddingValues,
    onNavigationClick: (String) -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    viewModel: MineScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val isTablet = isTablet()
    val aspectRadio = getAspectRadio()
    val icon = remember {
        when (state.themeMode) {
            1 -> AgeAnimeIcons.light
            2 -> AgeAnimeIcons.dark
            else -> AgeAnimeIcons.autoMode
        }
    }
    val context = LocalContext.current
    val userName = rememberMutableStateOf(value = "")
    val passWork = rememberMutableStateOf(value = "")
    val captcha = rememberMutableStateOf(value = "")
    var playerSettingVisibility by rememberMutableStateOf(value = false)
    var autoFullscreen by rememberSavableMutableStateOf(value = Settings.autoFullscreen)
    var playSkipTime by rememberSavableMutableStateOf(value = Settings.playSkipTime)

    AgeScaffold(
        modifier = Modifier.padding(padding),
        state = state,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "${greeting(LocalTime.now())}${if (state.hideUserName) "" else state.userModel?.username?.ifBlank { "游客" } ?: "游客"}")
                },
                actions = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable(role = Role.Button, onClick = viewModel::changeThemeMode)
                            .padding(10.dp),
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            )
        },
        onShowSnackbar = onShowSnackbar,
        onRefresh = { },
        onErrorPositiveAction = {

        },
        onDismissErrorDialog = viewModel::hideError
    ) { paddingValues, _ ->

        val cardModifier = remember {
            Modifier
                .padding(1.dp)
                .clip(RoundedCornerShape(6.dp))
        }

        val itemModifier = remember {
            Modifier
                .fillMaxWidth()
                .height(if (isTablet) 48.dp else 38.dp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(15.dp, 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(if (aspectRadio > 1.8) 12.dp else 28.dp)
            ) {
                PrimaryBox(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(if (aspectRadio > 1.8) 1.25f else 1.8f)
                        .then(cardModifier),
                    iconId = AgeAnimeIcons.person,
                    text = "登录",
                    onClick = viewModel::getSpacaptcha
                )
                PrimaryBox(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(if (aspectRadio > 1.8) 1.25f else 1.8f)
                        .then(cardModifier),
                    iconId = AgeAnimeIcons.love,
                    text = "我的追番",
                    onClick = {
                        onNavigationClick("本地收藏")
                    }
                )
                PrimaryBox(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(if (aspectRadio > 1.8) 1.25f else 1.8f)
                        .then(cardModifier),
                    iconId = AgeAnimeIcons.history,
                    text = "历史观看",
                    onClick = {
                        onNavigationClick("历史记录")
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(cardModifier)
            ) {

                ItemWithSwitch(
                    modifier = itemModifier,
                    text = "夜间主题跟随系统",
                    checked = state.themeMode == 3,
                    onCheckedChange = viewModel::changeThemeMode
                )

                ItemWithSwitch(
                    modifier = itemModifier,
                    text = "隐藏用户名",
                    checked = state.hideUserName,
                    onCheckedChange = viewModel::changeHideUserName
                )

                ItemWithSwitch(
                    modifier = itemModifier,
                    text = "自动检查更新",
                    checked = state.autoCheckUpdate,
                    onCheckedChange = viewModel::changeAutoCheckUpdates
                )

                ItemWithAction(
                    modifier = itemModifier,
                    text = "播放设置",
                    action = {
                        playerSettingVisibility = true
                    }
                )

                ItemWithAction(
                    modifier = itemModifier,
                    text = "访问 GitHub 仓库",
                    action = {
                        context.openUrl(Api.AGE_GITHUB)
                    }
                )

                state.userModel?.takeIf { it.username.isNotBlank() }?.let {

                    ItemWithAction(
                        modifier = itemModifier,
                        text = "网络收藏",
                        action = { onNavigationClick("网络收藏") }
                    )

                    ItemWithNewPage(
                        modifier = itemModifier,
                        text = "退出登陆",
                        onClick = viewModel::logout
                    )
                }


            }
        }

        if (state.showLoginDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideLoginDialog,
                title = { Text("登录") },
                text = {
                    Column {
                        // 使用 OutlinedTextField 组件创建一个带边框的文本输入框，用于输入用户名
                        OutlinedTextField(
                            value = userName.value, // 绑定用户名的状态
                            onValueChange = userName::value::set, // 输入时更新用户名的状态
                            label = { Text(text = "用户名") }, // 输入框的标签
                            modifier = Modifier.fillMaxWidth() // 填充满宽度
                        )
                        // 使用 Spacer 组件创建一个间隔，高度为 8dp
                        Spacer(modifier = Modifier.height(8.dp))
                        // 使用 OutlinedTextField 组件创建一个带边框的文本输入框，用于输入密码
                        OutlinedTextField(
                            value = passWork.value, // 绑定密码的状态
                            onValueChange = passWork::value::set, // 输入时更新密码的状态
                            label = { Text(text = "密码") }, // 输入框的标签
                            modifier = Modifier.fillMaxWidth(), // 填充满宽度
                            visualTransformation = PasswordVisualTransformation() // 使用密码可视化转换，隐藏输入的字符
                        )
                        // 使用 Spacer 组件创建一个间隔，高度为 8dp
                        Spacer(modifier = Modifier.height(8.dp))
                        // 使用 Row 布局创建一个水平排列的行，用于显示验证码图片和输入框
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // 使用 Image 组件显示验证码图片，如果图片数据为空，则显示一个占位符文本
                            if (state.spacaptchaModel != null && state.spacaptchaModel!!.img.isNotBlank()) {
                                val img = state.spacaptchaModel!!.img.replace(
                                    "data:image/png;base64,",
                                    ""
                                ).toBitmap()
                                AsyncImage(
                                    model = img,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp, 40.dp)
                                        .clickable(onClick = viewModel::getSpacaptcha)
                                )
                            } else {
                                Text(
                                    text = "加载中...",
                                    modifier = Modifier.size(80.dp, 40.dp)
                                ) // 显示一个占位符文本，大小为 80dp x 40dp
                            }
                            // 使用 Spacer 组件创建一个间隔，宽度为 8dp
                            Spacer(modifier = Modifier.width(8.dp))
                            // 使用 OutlinedTextField 组件创建一个带边框的文本输入框，用于输入验证码
                            OutlinedTextField(
                                value = captcha.value, // 绑定验证码的状态
                                onValueChange = captcha::value::set, // 输入时更新验证码的状态
                                label = { Text(text = "验证码") }, // 输入框的标签
                                modifier = Modifier.weight(1f) // 设置权重为 1，使输入框占据剩余的空间
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (userName.value.length !in 1..16) {
                            // 如果用户名超过 16 位，则弹出一个提示框，提示用户名太长
                            viewModel.showError(
                                AgeException.AlertException(
                                    AlertDialogModel(
                                        title = "提示",
                                        message = "16个字符内的字母、数字或符号",
                                    )
                                )
                            )
                        } else if (!passWork.value.matches(Regex("^[\\w\\W]{8,32}$"))) {
                            // 如果密码不是 8-32 位字母、数字、符号，则弹出一个提示框，提示密码格式不正确
                            viewModel.showError(
                                AgeException.AlertException(
                                    AlertDialogModel(
                                        title = "提示",
                                        message = "8-32位字母、数字或符号",
                                    )
                                )
                            )
                        } else {
                            viewModel.login(
                                userName.value,
                                passWork.value,
                                captcha.value
                            )
                        }
                    }) { // 点击时触发登录的回调函数，并传入用户名、密码和验证码
                        Text(text = "登录") // 按钮的文本
                    }
                },
            )
        }

        if (playerSettingVisibility) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onDismissRequest = { playerSettingVisibility = false },
                title = { Text("播放设置") },
                text = {
                    Column {
                        ItemWithSwitch(
                            modifier = itemModifier,
                            text = "自动全屏",
                            checked = autoFullscreen,
                            onCheckedChange = {
                                autoFullscreen = it
                                Settings.autoFullscreen = it
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = "$playSkipTime",
                            onValueChange = {
                                if (it.isNotBlank()) {
                                    val i = it.toInt()
                                    playSkipTime = i
                                    Settings.playSkipTime = i
                                }
                            }, // 输入时更新用户名的状态
                            label = { Text(text = "播放快进时间") },
                            modifier = Modifier.fillMaxWidth()
                        )

                    }
                },
                confirmButton = {},
            )
        }

    }

}

@Composable
private fun ItemWithSwitch(
    text: String,
    checked: Boolean?,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onCheckedChange(checked?.not() ?: false) }
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
            AnimatedSwitchButton(
                modifier = Modifier.size(48.dp),
                checked = checked
            )
        }

    }

}

@Composable
private fun PrimaryBox(
    modifier: Modifier,
    iconId: Int,
    text: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .clickable(
                interactionSource = rememberMutableInteractionSource(),
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun ItemWithNewPage(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                painter = painterResource(AgeAnimeIcons.arrowRight),
                contentDescription = "more",
            )
        }
    }

}

@Composable
private fun ItemWithAction(
    modifier: Modifier = Modifier,
    text: String,
    action: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = action)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun greeting(currentTime: LocalTime) = currentTime.run {
    when {
        isBefore(LocalTime.of(6, 0)) -> "晚上好！"
        isBefore(LocalTime.of(8, 30)) -> "早上好！"
        isBefore(LocalTime.NOON) -> "上午好！"
        isBefore(LocalTime.of(13, 0)) -> "中午好！"
        isBefore(LocalTime.of(18, 0)) -> "下午好！"
        else -> "晚上好！"
    }
}