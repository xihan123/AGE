package cn.xihan.age.ui.main.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.network.SPSettings
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.Utils
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logDebug
import com.hadiyarajesh.flower_core.networkResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/6/4 14:34
 * @介绍 :
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<CatalogState, IUiIntent>() {

    val type: String = savedStateHandle["type"] ?: "all"

    private var filterList: MutableMap<String, String> = mutableMapOf(
        "genre" to "all",
        "label" to type,
        "letter" to "all",
        "order" to "更新时间",
        "resource" to "all",
        "season" to "all",
        "status" to "all",
        "year" to "all",
        "region" to "all"
    )

    override fun initViewState(): CatalogState = CatalogState()

    /**
     * 1秒内不可重复执行
     */
    private var lastClickTime = 0L

    /**
     * 获取 * 秒内不可重复执行
     * @param time 间隔时间
     */
    fun isFastClick(
        time: Long = 1500,
    ): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeD = currentTime - lastClickTime
        if (timeD in 1 until time) {
            return true
        }

        lastClickTime = currentTime
        return false
    }

    private fun getFilterCategoryModel() = intent {
        reduce {
            state.copy(
                catalogFlow = remoteRepository.getFilterCategoryModel(filterList).flow.cachedIn(
                    viewModelScope
                )
            )
        }
    }

    /**
     * 更改筛选条件
     */
    fun changeFilterCategory(key: String, value: String) = intent {
        Utils.updateMap(map = filterList, key = key, value = value)
        if (!isFastClick()){
            getFilterCategoryModel()
        }
    }

    init {
        logDebug("type:$type")
        getFilterCategoryModel()
    }

}

data class CatalogState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val catalogFlow: Flow<PagingData<CatalogModel.AniPreLModel>> = flowOf(PagingData.empty())
) : IUiState