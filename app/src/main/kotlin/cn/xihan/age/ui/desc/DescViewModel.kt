package cn.xihan.age.ui.desc

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.extension.AgeException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/25 7:04
 * @介绍 :
 */
@HiltViewModel
class DescViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<DescState, IUiIntent>() {

    val animeId: Int = checkNotNull(savedStateHandle["animeId"])

    override fun initViewState(): DescState = DescState()

    fun getAnimeDetailModel() = intent {
        showLoading()
        remoteRepository.getAnimeDetailModel(animeId).collect { animeDetailModel ->
            val animeId = animeDetailModel.video.id
            val animeCover =
                animeDetailModel.video.cover
            localRepository.upsertFavoriteModel(
                animeId = animeId,
                animeName = animeDetailModel.video.name,
                animeCover = animeCover,
                animeSubtitle = animeDetailModel.video.uptodate
            )
            localRepository.upsertAnimeDetailModel(animeDetailModel.video)
            reduce {
                state.copy(
                    currentAnimeDetailModel = animeDetailModel,
                    loading = false,
                    refreshing = false,
                    error = null
                )
            }
        }
    }

    fun updateAnimeFavorites(favorite: Boolean) = intent {
        localRepository.updateFavorite(animeId, favorite)
    }

    fun showLoading() {
        intent {
            reduce {
                state.copy(
                    loading = true,
                    refreshing = true
                )
            }
        }
    }

    override fun showError(error: AgeException) {
        intent {
            reduce {
                state.copy(
                    loading = false,
                    refreshing = false,
                    error = error
                )
            }
        }
    }

    override fun hideError() {
        intent {
            reduce {
                state.copy(
                    loading = false,
                    refreshing = false,
                    error = null
                )
            }
        }
    }

    init {
        getAnimeDetailModel()
        intent {
            localRepository.isFavorite(animeId).collect {
                reduce {
                    state.copy(
                        favorite = it
                    )
                }
            }
        }
    }


}

data class DescState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val favorite: Boolean = false,
    val currentAnimeDetailModel: AnimeDetailModel? = null,
) : IUiState