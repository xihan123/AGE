package cn.xihan.age.ui.player

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.model.CommentResponseModel
import cn.xihan.age.model.Video
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.error
import cn.xihan.age.util.indexOfKey
import cn.xihan.age.util.nullList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/22 18:01
 * @介绍 :
 */
@HiltViewModel
class AnimePlayViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<AnimePlayViewState, AnimePlayUiIntent>() {

    val animeId: Int = checkNotNull(savedStateHandle["animeId"])

    override val container: Container<AnimePlayViewState, AnimePlayUiIntent> by lazy {
        container(
            initialState = initViewState(),
            savedStateHandle = savedStateHandle,
            buildSettings = {
                exceptionHandler = coroutineExceptionHandler
            }
        )
    }

    override fun initViewState(): AnimePlayViewState = AnimePlayViewState()

    fun refresh() {
        queryAnimeModel()
        queryAnimeCommentModel()
    }

    fun queryAnimeModel() = intent {
        reduce {
            state.copy(
                loading = true,
                video = Video(),
                series = nullList(5),
                similar = nullList(12),
                error = null
            )
        }
        remoteRepository
            .getAnimeDetailModel(animeId)
            .error(this@AnimePlayViewModel)
            .onEmpty { reduce { state.copy(refreshing = false, loading = false) } }
            .onEach {
                reduce {
                    state.copy(
                        video = it.video,
                        series = it.series,
                        similar = it.similar,
                        playerLabelMap = it.playerLabel,
                        refreshing = false,
                        loading = false
                    )
                }
                with(it.video) {
                    localRepository.upsertFavoriteModel(
                        animeId = animeId,
                        animeName = name,
                        animeCover = cover,
                        animeSubtitle = upToDate
                    )
                }
            }
            .onCompletion {
                if (state.video.id != 0) {
                    queryHistoryProgress()
                }
            }
            .collect()
    }

    fun queryAnimeCommentModel() = intent {
        reduce {
            state.copy(
                commentModelPagingData = remoteRepository.getAnimeCommentModel(animeId).flow
                    .flowOn(Dispatchers.IO)
                    .cachedIn(
                        viewModelScope
                    )
            )
        }
    }

    fun getVideoSource(
        playIndex: Int = 0,
        episodeIndex: Int = 0,
        episodeTitle: String = "",
        playType: String = ""
    ) = intent {
        val copyEpisodeIndex = if (episodeIndex == 0) 1 else episodeIndex.plus(1)
        val copyPlayIndex = if (playIndex == 0) 1 else playIndex.plus(1)
        val playList =
            state.video.playlists.entries.first { it.key == playType }.value.map { it.first() }
        reduce {
            state.copy(
                playIndex = playIndex,
                playType = playType.ifBlank { state.video.playlists.keys.first() },
                playList = playList,
                episodeIndex = episodeIndex,
                episodeTitle = episodeTitle.ifBlank { playList[episodeIndex] }
            )
        }

        remoteRepository
            .getVideoSource(
                animeId,
                playIndex = copyPlayIndex,
                episodeIndex = copyEpisodeIndex
            )
            .error(this@AnimePlayViewModel)
            .onEach {
                if (it.isNotBlank()) {
                    postSideEffect(AnimePlayUiIntent.UpdateVideoUrl(it))
                }
            }
            .collect()
    }

    /**
     * 保存播放进度
     */
    fun saveHistoryProgress(
        position: Long = 0L,
        duration: Long = 0L
    ) = intent {
        if (duration == 0L || state.video.id == 0) return@intent
        localRepository.updateHistory(
            animeId = animeId,
            animeName = state.video.name,
            animeCover = state.video.cover,
            animeLatestPlayTitle = state.playList.last() ?: "",
            animeLastPlayTitle = state.playList[state.episodeIndex] ?: "",
            animePlayListType = state.playType,
            animeLastPlayProgress = position,
            animeLastPlayDuration = duration
        )
    }

    fun queryHistoryProgress() = intent {
        localRepository.queryHistoryByAnimeId(animeId = animeId)?.let { historyModel ->
            val playType = historyModel.animePlayListType
            val playIndex = state.video.playlists.indexOfKey(playType)
            val lastPlayTitle = historyModel.animeLastPlayTitle
            val playList =
                state.video.playlists.entries.first { it.key == playType }.value.map { it.first() }
            getVideoSource(
                playIndex = playIndex,
                episodeIndex = playList.indexOf(lastPlayTitle),
                episodeTitle = lastPlayTitle,
                playType = playType
            )
        } ?: run {
            val playType = state.playerLabelMap.keys.first()
            val episodeTitle =
                state.video.playlists.getValue(playType).first { it.first().isNotBlank() }
                    .first()
            val playList =
                state.video.playlists.entries.first { it.key == playType }.value.first { it.first() == episodeTitle }
            getVideoSource(
                playIndex = 0,
                episodeIndex = playList.indexOf(episodeTitle),
                episodeTitle = episodeTitle,
                playType = playType
            )
        }

    }

    fun getHistoryPosition() = intent {
        localRepository.queryHistoryByAnimeId(animeId = animeId)?.let { historyModel ->
            val lastPlayProgress = historyModel.animeLastPlayProgress
            val lastPlayDuration = historyModel.animeLastPlayDuration
            val playType = historyModel.animePlayListType
            val lastPlayTitle = historyModel.animeLastPlayTitle
            if (state.episodeTitle == lastPlayTitle) {
                postSideEffect(
                    AnimePlayUiIntent.UpdateVideoProgress(
                        lastPlayProgress,
                        lastPlayDuration
                    )
                )
            } else {
                postSideEffect(AnimePlayUiIntent.UpdateVideoProgress(0L, 0L))
            }
        } ?: postSideEffect(AnimePlayUiIntent.UpdateVideoProgress(0L, 0L))
    }

    fun queryFavoriteState() = localRepository.queryFavorite(animeId)

    fun queryAutoFullScreenState() = intent {
        postSideEffect(AnimePlayUiIntent.AutoFullscreen)
    }

    fun updateAnimeFavorites(favorite: Boolean) = intent {
        localRepository.updateFavorite(animeId, favorite)
    }

    fun unlockOrientation() = intent {
        reduce {
            state.copy(orientationRequest = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        }
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

    init {
        refresh()
    }

}

@Parcelize
data class AnimePlayViewState(
    override var loading: Boolean = true,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val video: Video = Video(),
    val series: List<AnimeModel?> = nullList(3),
    val similar: List<AnimeModel?> = nullList(12),
    val playerLabelMap: Map<String, String> = emptyMap(),
    val playIndex: Int = 1,
    val playType: String = "",
    val episodeTitle: String = "1",
    val episodeIndex: Int = 1,
    val playList: List<String?> = nullList(5),
    @IgnoredOnParcel
    val commentModelPagingData: Flow<PagingData<CommentResponseModel.DataModel.CommentModel>> = flowOf(
        PagingData.empty()
    ),
    val orientationRequest: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
) : IUiState, Parcelable

sealed interface AnimePlayUiIntent : IUiIntent {
    data object AutoFullscreen : AnimePlayUiIntent
    data class UpdateVideoProgress(val position: Long, val duration: Long) : AnimePlayUiIntent
    data class UpdateVideoUrl(val videoUrl: String) : AnimePlayUiIntent
}



