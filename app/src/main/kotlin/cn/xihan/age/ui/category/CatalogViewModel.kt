package cn.xihan.age.ui.category

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.model.Category
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/27 20:47
 * @介绍 :
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<CatalogState, IUiIntent>() {

    val label = savedStateHandle.get<String>("label") ?: ""

    val filter = mutableStateMapOf(
        Category.Region to ("all" to "全部"),
        Category.Label to ("all" to "全部"),
        Category.Letter to ("all" to "全部"),
        Category.Year to ("all" to "全部"),
        Category.Season to ("all" to "全部"),
        Category.Status to ("all" to "全部"),
        Category.Genre to ("all" to "全部"),
        Category.Resource to ("all" to "全部"),
        Category.Order to ("time" to "时间排序"),
    )

    /**
     * 1秒内不可重复执行
     */
    private var lastClickTime = 0L

    /**
     * 获取 * 秒内不可重复执行
     * @param time 间隔时间
     */
    private fun isFastClick(
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


    override fun initViewState(): CatalogState = CatalogState()

    override fun hideError() {
        intent {
            reduce {
                state.copy(error = null)
            }
        }
    }

    fun getFilterCategoryModel() = intent {
        val filterList = filter.map { ("${it.key}".lowercase()) to it.value.first }.toMap()
        reduce {
            state.copy(
                loading = false,
                refreshing = false,
                error = null,
                catalogFlow = remoteRepository.getFilterCategoryModel(filterList).flow.cachedIn(
                    viewModelScope
                )
            )
        }
    }

    /**
     * 更改筛选条件
     */
    fun changeFilterCategory(category: Category, pair: Pair<String, String>) = intent {
        filter[category] = pair
        if (!isFastClick()) {
            getFilterCategoryModel()
        }
    }

    init {
        intent {
            delay(100)
            if (label.isNotBlank())
                changeFilterCategory(Category.Label, label to label)
            else
                getFilterCategoryModel()

        }
    }

}


data class CatalogState(
    override var loading: Boolean = true,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val catalogFlow: Flow<PagingData<CatalogModel.VideoModel>> = flowOf(PagingData.empty())
) : IUiState