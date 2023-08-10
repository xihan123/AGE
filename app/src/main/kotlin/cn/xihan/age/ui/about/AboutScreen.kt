package cn.xihan.age.ui.about

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.xihan.age.R
import cn.xihan.age.component.IconTextSetting
import cn.xihan.age.network.Api
import cn.xihan.age.network.Settings
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.Utils.showDisclaimers
import cn.xihan.age.util.VerticalSpace
import cn.xihan.age.util.isNetworkAvailable
import cn.xihan.age.util.openUrl
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/6/5 22:35
 * @介绍 :
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    appState: MainAppState
) {
    val context = LocalContext.current
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(), topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(id = R.string.about))
        }, modifier = Modifier.fillMaxWidth(), actions = {

        }, navigationIcon = {
            IconButton(onClick = {
                appState.popBackStack()
            }) {
                Icon(Icons.Filled.KeyboardArrowLeft, null)
            }
        }, scrollBehavior = null
        )

    }) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopCenter),
            ) {

                item("header") {

                    VerticalSpace(dp = 15.dp)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher),
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                item("buildInfo") {

                    VerticalSpace(dp = 25.dp)

                    Text(
                        text = stringResource(
                            id = R.string.about_BuildInfo,
                            Utils.getASVersionName(),
                            "231.9225",
                            "gradle-8.2.1-bin",
                            Utils.getBuildTimeDescription()
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )

                }

                item("other") {
                    VerticalSpace(dp = 20.dp)

                    IconTextSetting(title = stringResource(id = R.string.about_AccessWebsite),
                        rightIcon = { Icon(Icons.Filled.KeyboardArrowRight, null) },
                        onClick = { Api.WEB_URL.openUrl() })

                    IconTextSetting(title = stringResource(id = R.string.disclaimers_title),
                        rightIcon = { Icon(Icons.Filled.KeyboardArrowRight, null) },
                        onClick = {
                            if (context.isNetworkAvailable()) {
                                appState.navigateToWeb(Api.INTRO_URL)
                            } else {
                                context.showDisclaimers()
                            }

                        })

                    IconTextSetting(title = stringResource(id = R.string.about_AccessWebsite),
                        rightIcon = { Icon(Icons.Filled.KeyboardArrowRight, null) },
                        onClick = { Api.GITHUB_URL.openUrl() })

                    IconTextSetting(title = stringResource(id = R.string.about_SyncInfo),
                        rightIcon = { Icon(Icons.Filled.KeyboardArrowRight, null) },
                        onClick = { Api.SYNC_INFO_URL.openUrl() })

                    IconTextSetting(title = stringResource(id = R.string.about_CheckUpdate),
                        rightIcon = {},
                        onClick = {
                            val updateModel = Settings.mainConfigModel.updateEntity
                            val newVersionName: String = updateModel.versionName
                            val newVersionCode = updateModel.versionCode.toInt()
                            val updateLog = updateModel.body
                            MessageDialog.show(
                                "${context.getString(R.string.find_new_version)} $newVersionName",
                                updateLog,
                                context.getString(R.string.update_now),
                                context.getString(R.string.update_after)
                            ).setOkButtonClickListener { _, _ ->
                                updateModel.browserDownloadUrl.openUrl()
                                true
                            }
                                .setOtherButton(context.getString(R.string.about_CloudDownloads)) { _, _ ->
                                    updateModel.onlineDiskDownloadLink.openUrl()
                                    PopTip.show(
                                        String.format(
                                            context.getString(R.string.about_CloudDownloadsPass),
                                            updateModel.onlineDiskDownloadLink.substringAfter("?password=")
                                        )
                                    )
                                    true
                                }
                        })

                }

            }

            Text(
                text = stringResource(id = R.string.about_Copyright, Utils.getYear()),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
                textAlign = TextAlign.Center
            )

        }

    }

}