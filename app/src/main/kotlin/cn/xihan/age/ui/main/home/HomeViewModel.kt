package cn.xihan.age.ui.main.home

import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiState
import cn.xihan.age.base.LoadingIntent
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.model.Banner
import cn.xihan.age.model.HomeModel
import cn.xihan.age.model.WeekItem
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.error
import cn.xihan.age.util.kJson
import cn.xihan.age.util.nullList
import cn.xihan.age.util.nullWeekMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty

import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 19:35
 * @介绍 :
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : BaseViewModel<HomeState, LoadingIntent>() {

    override fun initViewState(): HomeState = HomeState()

    fun getHomePage() = intent {
        reduce {
            state.copy(
                latest = nullList(12),
                recommend = nullList(12),
                bannerList = nullList(5),
                weekItems = nullWeekMap(5)
            )
        }

        merge(
            remoteRepository.getHomePageModel(),
            remoteRepository.getBannerModel()
        )
            .error(this@HomeViewModel)
            .onEmpty { reduce { state.copy(refreshing = false, loading = false) } }
            .onEach {
                when (it) {
                    is HomeModel -> reduce {
                        state.copy(
                            latest = it.latest,
                            recommend = it.recommend,
                            weekItems = it.weekItems.mapKeys { (key, _) -> key.toInt() }
                        )
                    }

                    is String -> reduce { state.copy(bannerList = kJson.decodeFromString(it)) }
                }
                reduce { state.copy(refreshing = false, loading = false) }
            }
            .collect()
    }

    override fun showError(error: AgeException) {
        intent {
            reduce {
                state.copy(error = error)
            }
        }
    }

    override fun hideError() {
        intent {
            reduce {
                state.copy(error = null)
            }
        }
    }

    fun updateSearchState(boolean: Boolean) = intent {
        reduce {
            state.copy(
                isSearching = boolean
            )
        }
    }

    init {
        getHomePage()
    }

}

data class HomeState(
    override var refreshing: Boolean = false,
    override var loading: Boolean = false,
    override var error: AgeException? = null,
    val latest: List<AnimeModel?> = nullList(12),
    val recommend: List<AnimeModel?> = nullList(12),
    val weekItems: Map<Int, List<WeekItem?>> = nullWeekMap(5),
    val bannerList: List<Banner.BannerItemModel?> = nullList(5),
    var isSearching: Boolean = false,
) : IUiState

