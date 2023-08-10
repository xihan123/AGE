package cn.xihan.age.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.EmptyList
import cn.xihan.age.component.ListHistorySearch
import cn.xihan.age.component.SearchByTextAppBar
import cn.xihan.age.component.Title
import cn.xihan.age.component.VerticalAnimeGridCardItem
import cn.xihan.age.component.items
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.work.AnimeDataWorker
import com.skydoves.whatif.whatIfNotNullOrEmpty
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/24 21:04
 * @介绍 :
 */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    appState: MainAppState
) {

    val context = LocalContext.current
    val state by viewModel.collectAsState()
    var query by rememberMutableStateOf(value = "")

    val searchList = state.searchFlow.collectAsLazyPagingItems()

    AgeScaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        state = state,
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissErrorDialog = { viewModel.hideError() },
        topBar = {
            SearchByTextAppBar(
                text = query,
                placeholder = state.searchHistoryList,
                onTextChange = {
                    query = it
                    viewModel.queryAnimeData(it)
                },
                onClickBack = { appState.popBackStack() },
                onClickSearch = {
                    viewModel.queryAnimeData(query)
                },
            )
        },
    ) { _, _ ->
        Column {

            Title(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                    .fillMaxWidth(),
                textTitle = stringResource(id = R.string.history),
                textAction = stringResource(id = R.string.clear_all),
                onClickAction = {
                    viewModel.clearSearchHistory()
                }
            )

            state.searchHistoryList.whatIfNotNullOrEmpty(
                whatIf = {
                    ListHistorySearch(
                        modifier = Modifier.fillMaxWidth(),
                        onClickHistoryItem = { historyItem ->
                            query = historyItem
                            viewModel.queryAnimeData(query)
                        },
                        listHistory = it,
                    )
                },
                whatIfNot = {
                    EmptyList(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.error_empty),
                    )
                }
            )

            Title(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                    .fillMaxWidth(),
                textTitle = String.format(
                    stringResource(id = R.string.search_result),
                    query
                ),
                textAction = stringResource(id = R.string.about_CheckUpdate),
                onClickAction = {
                    val getAnimeDataWorkRequest = OneTimeWorkRequestBuilder<AnimeDataWorker>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build()
                    WorkManager.getInstance(context).enqueue(getAnimeDataWorkRequest)
                }
            )

            if (searchList.itemCount == 0) {
                EmptyList(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.error_empty),
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = searchList,
                        key = { item -> item.id },
                        span = { GridItemSpan(1) }
                    ) { item ->
                        item?.let { item ->
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

    }

}