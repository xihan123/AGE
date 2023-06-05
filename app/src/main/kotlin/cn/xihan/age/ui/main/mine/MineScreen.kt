package cn.xihan.age.ui.main.mine

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import cn.xihan.age.R
import cn.xihan.age.component.BottomSheetDialog
import cn.xihan.age.component.BottomSheetDialogProperties
import cn.xihan.age.component.CardIconButton
import cn.xihan.age.network.SPSettings
import cn.xihan.age.network.Settings
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.VerticalSpace
import cn.xihan.age.util.extension.topActivity
import cn.xihan.age.util.rememberMutableStateOf
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/12 8:57
 * @介绍 :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineScreen(
    appState: MainAppState,
    viewModel: MineViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.collectAsState()
    var expanded by rememberMutableStateOf(value = false)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                modifier = Modifier.fillMaxWidth(), actions = {},
                navigationIcon = {},
                scrollBehavior = null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    //horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoilImage(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(20.dp)
                            .clip(RoundedCornerShape(360.dp)),
                        imageModel = { R.drawable.mytx },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    )
                    /*
                                        Text(
                                            modifier = Modifier.clickable {
                                                appState.navigateToLogin()
                                            },
                                            text = if (state.userInfoModel?.username.isNotNullOrBlank())
                                                String.format(
                                                    stringResource(id = R.string.user_name),
                                                    state.userInfoModel!!.username
                                                ) else stringResource(id = R.string.not_login)
                                        )

                     */

                }

            }

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    CardIconButton(
                        text = stringResource(id = R.string.reward),
                        icon = painterResource(id = R.drawable.paid),
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    CardIconButton(
                        text = stringResource(id = R.string.collect),
                        icon = painterResource(id = R.drawable.grade),
                        onClick = {
                            appState.navigateToFavorite()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    CardIconButton(
                        text = stringResource(id = R.string.history),
                        icon = painterResource(id = R.drawable.history),
                        onClick = {
                            appState.navigateToHistory()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.isFollowVisible) {
                        CardIconButton(
                            text = stringResource(id = R.string.follow),
                            icon = painterResource(id = R.drawable.update),
                            onClick = {
                                // TODO: 追番页面

                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    CardIconButton(
                        text = stringResource(id = R.string.setting),
                        icon = painterResource(id = R.drawable.settings),
                        onClick = {
                            appState.navigateToSettings()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )


                }


            }

            /*
            if (state.userInfoModel != null) {
                Button(
                    modifier = M
                        .padding(8.dp)
                        .fillMaxWidth(),
                    onClick = {
                        viewModel.logout()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.logout)
                    )
                }
            }

             */

            if (expanded) {
                BottomSheetDialog(
                    onDismissRequest = {
                        expanded = false
                    }, properties = BottomSheetDialogProperties(
                        dismissOnBackPress = true, dismissOnClickOutside = true
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.reward_text))

                        VerticalSpace(dp = 18.dp)

                        Row(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.zfb2),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .size(200.dp)
                            )

                            Button(onClick = {
                                Utils.startAlipayClient(
                                    activity = topActivity,
                                    payCode = Settings.mainConfigModel.appEntity.payCode,
                                )
                            }) {
                                Text(text = stringResource(id = R.string.alipay))
                            }

                        }

                        VerticalSpace(dp = 18.dp)

                        Row(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wx2),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(20.dp)
                                    .size(200.dp)
                            )

                            Button(onClick = {
                                Utils.saveImageToGallery(
                                    context = topActivity,
                                    bitmap = BitmapFactory.decodeResource(
                                        topActivity.resources,
                                        R.drawable.wx2
                                    )
                                )
                                Utils.startWeChatClient(
                                    c = topActivity
                                )
                            }) {
                                Text(text = stringResource(id = R.string.wechat))
                            }
                        }
                    }

                }

            }


        }

    }


}