package cn.xihan.age.ui.main.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.BottomSheetDialog
import cn.xihan.age.component.BottomSheetDialogProperties
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.HorizontalCatalogItems
import cn.xihan.age.component.VerticalAnimeGridCardItem
import cn.xihan.age.component.items
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.rememberMutableStateOf
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/23 18:53
 * @介绍 :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    appState: MainAppState,
    type: String? = "",
    animeId: String? = "",
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val catalogList = state.catalogFlow.collectAsLazyPagingItems()
    var expanded by rememberMutableStateOf(value = false)

    AgeScaffold(
        state = state,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = if (type.isNullOrBlank()) stringResource(id = R.string.app_name) else type)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                actions = {
                    IconButton(onClick = {
                        expanded = !expanded
                    }) {
                        Icon(Icons.Filled.List, null)
                    }
                },
                navigationIcon = {
                    if (!type.isNullOrBlank()) {
                        IconButton(onClick = {
                            appState.popBackStack()
                        }) {
                            Icon(Icons.Filled.KeyboardArrowLeft, null)
                        }
                    }
                },
                scrollBehavior = null
            )
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        }
    ) { _, _ ->
        Column {
            if (catalogList.itemCount == 0) {
                ErrorItem(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp), errorMessage = stringResource(id = R.string.error_empty)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = catalogList,
                        key = { item -> item.id },
                        span = { GridItemSpan(1) }
                    ) { item ->
                        item?.let {
                            VerticalAnimeGridCardItem(
                                modifier = Modifier
                                    .wrapContentHeight()
//                                    .fillParentMaxWidth(.3f)
                                    .padding(4.dp),
                                animeId = item.id,
                                title = item.name,
                                subtitle = item.uptodate,
                                cover = item.cover,
                                appState = appState
                            )
                        }
                    }
                }
            }
        }
        if (expanded) {
            BottomSheetDialog(
                onDismissRequest = {
                    expanded = false
                },
                properties = BottomSheetDialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    HorizontalCatalogItems(
                        data = Utils.analysisLabels(0),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.changeFilterCategory("region", it)
                    }

                    HorizontalCatalogItems(
                        data = Utils.analysisLabels(1),
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.changeFilterCategory("genre", it)
                    }

                    HorizontalCatalogItems(
                        modifier = Modifier.weight(1f),
                        data = Utils.analysisLabels(2)
                    ) {
                        viewModel.changeFilterCategory("letter", it)
                    }
                    HorizontalCatalogItems(
                        modifier = Modifier.weight(1f),
                        data = Utils.analysisLabels(3)
                    ) {
                        viewModel.changeFilterCategory("year", it)
                    }

                    HorizontalCatalogItems(
                        modifier = Modifier.weight(1f),
                        data = Utils.analysisLabels(4)
                    ) {
                        viewModel.changeFilterCategory("season", it)
                    }

                    HorizontalCatalogItems(
                        modifier = Modifier.weight(1f),
                        data = Utils.analysisLabels(5)
                    ) {
                        viewModel.changeFilterCategory("status", it)
                    }

                    val labelList = Utils.analysisLabels(6)
                    HorizontalCatalogItems(
                        modifier = Modifier.weight(1f),
                        initialPage = Utils.getLabelsIndex(type, labelList),
                        data = labelList
                    ) {
                        viewModel.changeFilterCategory("label", it)
                    }

                    HorizontalCatalogItems(
                        modifier = Modifier.weight(1f),
                        data = Utils.analysisLabels(7)
                    ) {
                        viewModel.changeFilterCategory("resource", it)
                    }

                    HorizontalCatalogItems(
                        modifier = Modifier.weight(1f),
                        data = Utils.analysisLabels(8)
                    ) {
                        viewModel.changeFilterCategory("order", it)
                    }


                }
            }

        }
    }

}