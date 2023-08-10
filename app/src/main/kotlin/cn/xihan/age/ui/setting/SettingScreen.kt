package cn.xihan.age.ui.setting

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import cn.xihan.age.R
import cn.xihan.age.component.BottomSheetDialog
import cn.xihan.age.component.BottomSheetDialogProperties
import cn.xihan.age.component.EditTextSetting
import cn.xihan.age.component.IconSwitchSetting
import cn.xihan.age.component.IconTextSetting
import cn.xihan.age.network.SPSettings
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.extension.thread
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.utils.CacheDataManager

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/6/5 16:30
 * @介绍 :
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    appState: MainAppState
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val followSystemTheme = rememberMutableStateOf(value = SPSettings.themeMode == 3)
    val nightMode = rememberMutableStateOf(value = SPSettings.themeMode == 1)
    val safeDns = rememberMutableStateOf(value = SPSettings.safeDns)
    val safeDnsAddress = rememberMutableStateOf(value = SPSettings.safeDnsIp)
    val safeDnsExpanded = rememberMutableStateOf(value = false)
    val safeDnsDnsList = remember {
        listOf(
            "210.2.4.8",
            "223.5.5.5",
            "119.29.29.29",
            "114.114.114.114",
            "1.1.1.1",
            "8.8.8.8",
            "9.9.9.9"
        )
    }
    val customApi = rememberMutableStateOf(value = SPSettings.customAPI)
    val customApiExpanded = rememberMutableStateOf(value = false)
    val customApiName = rememberMutableStateOf(value = SPSettings.customApiName)
    val customApiAddress = rememberMutableStateOf(value = SPSettings.customApiUrl)
    val customApiList = remember {
        Utils.getCustomApiList()
    }

    val enableX5 = rememberMutableStateOf(value = SPSettings.enableX5)
    val x5Available = rememberMutableStateOf(value = SPSettings.x5Available)

    val cacheSizeText =
        rememberMutableStateOf(value = CacheDataManager.getTotalCacheSize(context))

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = stringResource(id = R.string.setting))
            }, modifier = Modifier.fillMaxWidth(), actions = {
                TextButton(onClick = {
                    appState.navigateToAbout()
                }) {
                    Text(
                        text = stringResource(id = R.string.about),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = {
                    appState.popBackStack()
                }) {
                    Icon(Icons.Filled.KeyboardArrowLeft, null)
                }
            }, scrollBehavior = null
            )

        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                item("followSystemTheme") {
                    IconSwitchSetting(
                        title = stringResource(id = R.string.setting_FollowSystemTheme),
                        checked = followSystemTheme,
                        onClick = {
                            Utils.changeThemeMode(
                                followSystemTheme = followSystemTheme.value,
                                nightMode = nightMode.value
                            )
                        },
                        onCheckedChange = {
                            Utils.changeThemeMode(
                                followSystemTheme = followSystemTheme.value,
                                nightMode = nightMode.value
                            )
                        })
                }
            }

            if (!followSystemTheme.value) {
                item("nightMode") {
                    IconSwitchSetting(
                        title = if (nightMode.value) stringResource(id = R.string.theme_night) else stringResource(
                            id = R.string.theme_day
                        ),
                        checked = nightMode,
                        onClick = {
                            Utils.changeThemeMode(
                                followSystemTheme = followSystemTheme.value,
                                nightMode = nightMode.value
                            )
                        },
                        onCheckedChange = {
                            Utils.changeThemeMode(
                                followSystemTheme = followSystemTheme.value,
                                nightMode = nightMode.value
                            )
                        })
                }
            }

            item("safeDns") {
                IconSwitchSetting(
                    title = if (safeDns.value) stringResource(
                        id = R.string.setting_SafeDnsTip,
                        safeDnsAddress.value
                    ) else stringResource(id = R.string.setting_SafeDns),
                    checked = safeDns,
                    onClick = {
                        safeDnsExpanded.value = true
                    },
                    onCheckedChange = {
                        SPSettings.safeDns = safeDns.value
                    },
                    enabledAutoChange = false
                )
            }

            item("enableX5"){
                IconSwitchSetting(
                    title = if (enableX5.value) stringResource(
                        id = R.string.setting_X5KernelStatus,
                        x5Available.value
                    ) else stringResource(id = R.string.setting_EnableX5Kernel),
                    checked = enableX5,
                    onClick = {
                        appState.navigateToWeb()
                    },
                    onCheckedChange = {
                        SPSettings.enableX5 = enableX5.value
                    },
                    enabledAutoChange = false
                )
            }

            if (safeDnsExpanded.value) {
                item("safeDnsExpanded") {
                    BottomSheetDialog(
                        onDismissRequest = {
                            safeDnsExpanded.value = false
                        },
                        properties = BottomSheetDialogProperties(
                            dismissOnBackPress = true, dismissOnClickOutside = true
                        )
                    ) {
                        LazyColumn {
                            items(
                                safeDnsDnsList.size,
                                key = {
                                    safeDnsDnsList[it]
                                }
                            ) {
                                TextButton(
                                    onClick = {
                                        safeDnsAddress.value = safeDnsDnsList[it]
                                        SPSettings.safeDnsIp = safeDnsDnsList[it]
                                        safeDnsExpanded.value = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),

                                    content = {
                                        Text(
                                            text = safeDnsDnsList[it],
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }

                            item {
                                EditTextSetting(
                                    title = "",
                                    safeDnsAddress,
                                ) {
                                    SPSettings.safeDnsIp = it
                                }
                            }
                        }
                    }
                }
            }

            item("customApi") {
                IconSwitchSetting(
                    title = if (customApi.value) stringResource(
                        id = R.string.setting_CustomApiTip,
                        customApiName.value
                    ) else stringResource(id = R.string.setting_CustomAPI),
                    checked = customApi,
                    onClick = {
                        customApiExpanded.value = true
                    },
                    onCheckedChange = {
                        SPSettings.customAPI = customApi.value
                    },
                    enabledAutoChange = false
                )
            }

            if (customApiExpanded.value) {
                item("customApiExpanded") {
                    BottomSheetDialog(
                        onDismissRequest = {
                            customApiExpanded.value = false
                        },
                        properties = BottomSheetDialogProperties(
                            dismissOnBackPress = true, dismissOnClickOutside = true
                        )
                    ) {
                        LazyColumn {
                            items(
                                customApiList.size,
                                key = {
                                    customApiList[it].hashCode()
                                }
                            ) {
                                TextButton(
                                    onClick = {
                                        customApiAddress.value = customApiList[it].apiUrl
                                        customApiName.value = customApiList[it].apiName
                                        SPSettings.customApiName = customApiList[it].apiName
                                        SPSettings.customApiUrl = customApiList[it].apiUrl
                                        SPSettings.customApiIndex = it
                                        customApiExpanded.value = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    content = {
                                        Text(
                                            text = customApiList[it].apiName,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }

                            item {
                                EditTextSetting(
                                    title = "",
                                    customApiAddress,
                                ) {
                                    SPSettings.customApiUrl = it
                                }
                            }
                        }
                    }
                }
            }

            item("clean"){
                IconTextSetting(
                    title = stringResource(id = R.string.setting_CleanUpCache),
                    subtitle = cacheSizeText.value
                ) {
                    thread {
                        CacheDataManager.clearAllCache(context)
                        cacheSizeText.value =
                            CacheDataManager.getTotalCacheSize(context)
                    }
                }
            }


        }

    }

}