package cn.xihan.age.ui.history

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.R
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.HistoryModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.SharedPreferencesUtil
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.mainThread
import com.kongzue.dialogx.dialogs.MessageDialog
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/16 10:30
 * @介绍 :
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<HistoryState, IUiIntent>() {

    override fun initViewState(): HistoryState = HistoryState()

    fun queryHistory(type: Int = 0) = intent {
        reduce {
            state.copy(
                history = localRepository.queryHistoryPaging(type).flow.cachedIn(viewModelScope)
            )
        }
    }

    /**
     * 根据 animeId 删除播放历史记录
     */
    fun deleteHistoryByAnimeId(animeId: String, isAll: Boolean = false) {
        MessageDialog.show(
            context.getString(android.R.string.dialog_alert_title),
            if (isAll) context.getString(R.string.delete_all) else context.getString(R.string.delete_single_tip),
            context.getString(android.R.string.ok),
            context.getString(android.R.string.cancel)
        ).setOkButton { _, _ ->
            intent {
                if (animeId.isBlank() && isAll) {
                    localRepository.deleteAllHistory()
                } else {
                    localRepository.deleteHistoryByAnimeId(animeId)
                }
                queryHistory()
            }
            false
        }


    }

    /**
     * 清空播放历史进度
     */
    fun deletePlayerHistoryProgress() {
        MessageDialog.show(
            context.getString(android.R.string.dialog_alert_title),
            context.getString(R.string.delete_player_all_history),
            context.getString(android.R.string.ok),
            context.getString(android.R.string.cancel)
        ).setOkButton { _, _ ->
            SharedPreferencesUtil("HistoryProgress").clearAll()
            false
        }
    }

    /**
     * 修改历史记录排序状态
     */
    fun changeHistorySortState(sortType: Int = 0) {
        queryHistory(sortType)
    }

    init {
        queryHistory()
    }

}


data class HistoryState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val history: Flow<PagingData<HistoryModel>> = flowOf(PagingData.empty())
) : IUiState