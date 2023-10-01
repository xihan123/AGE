package cn.xihan.age.ui.main.schedule

import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.Settings
import cn.xihan.age.util.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/28 15:58
 * @介绍 :
 */
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<ScheduleState, IUiIntent>() {

    override fun initViewState(): ScheduleState = ScheduleState()

    fun getUpdate() = intent {
        reduce {
            state.copy(loading = true)
        }
        remoteRepository.getWeeklyUpdate()
            .error(this@ScheduleViewModel)
            .combine(localRepository.queryAllFavoriteAnimeId()) { map, aids ->
                reduce {
                    state.copy(
                        loading = false,
                        refreshing = false,
                        error = null,
                        filterMap = when (state.filterType) {
                            ScheduleFilterType.ALL -> map
                            ScheduleFilterType.FOLLOWED -> map.mapValues { it.value.filter { it1 -> it1.aID in aids } }
                            ScheduleFilterType.SERIALIZING -> map.mapValues { it.value.filter { it1 -> "完结" !in it1.newTitle && "PV" != it1.newTitle } }
                        }
                    )
                }
            }
            .collect()
    }

    fun changeFilterType(filterType: ScheduleFilterType) = intent {
        Settings.scheduleFilterType = filterType.label
        reduce {
            state.copy(filterType = filterType)
        }
        getUpdate()
    }

    init {
        getUpdate()
    }

}

data class ScheduleState(
    override var loading: Boolean = true,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val filterType: ScheduleFilterType = ScheduleFilterType.of(Settings.scheduleFilterType),
    val filterMap: Map<String, List<AnimeModel>> = emptyMap()
) : IUiState

enum class ScheduleFilterType(
    val label: String
) {
    ALL("全部"),
    FOLLOWED("已追番"),
    SERIALIZING("连载中");

    companion object {
        fun of(value: String): ScheduleFilterType {
            return ScheduleFilterType.entries.first { it.label == value }
        }
    }
}
