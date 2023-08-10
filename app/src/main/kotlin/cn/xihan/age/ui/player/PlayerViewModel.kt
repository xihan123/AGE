package cn.xihan.age.ui.player

import android.content.Context
import android.view.View
import androidx.lifecycle.SavedStateHandle
import cn.xihan.age.R
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.component.player.VideoPlayerController
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.SpUtil
import cn.xihan.age.util.Utils
import cn.xihan.age.util.encodes
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.isWebUrl
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.playUrl
import cn.xihan.sniffing.SniffingUICallback
import cn.xihan.sniffing.SniffingVideo
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/27 8:36
 * @介绍 :
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<PlayerState, PlayerIntent>(), SniffingUICallback {

    val animeId: Int = checkNotNull(savedStateHandle["animeId"])
    val episodeType: String = checkNotNull(savedStateHandle["episodeType"])
    val episodeTitle: String = checkNotNull(savedStateHandle["episodeTitle"])

    override fun initViewState(): PlayerState = PlayerState()

    /**
     * 改变播放索引
     * @param [index] 索引
     */
    fun changePlayerIndex(
        index: Int = -1,
        videoPlayerController: VideoPlayerController? = null,
    ) = intent {
        val i = if (index == -1) state.currentAnimePlayerList.lastIndexOf(episodeTitle) else index

        val playUrl = playUrl(
            animeId = animeId, page = state.currentAnimePlayerType, index = i
        ) ?: return@intent

        val currentAnimePlayerEpisode = state.currentAnimePlayerList[i]
        val currentEpisodePosition = SpUtil("HistoryProgress").decodeLong(
            "$animeId/$currentAnimePlayerEpisode}", 0
        )
        reduce {
            state.copy(
                hasPreVideo = i != 0,
                hasNextVideo = i != state.currentAnimePlayerList.size - 1,
                currentPlayerIndex = i,
                currentOriginPlayerUrl = playUrl,
                currentEpisodeTitle = currentAnimePlayerEpisode,
                currentPosition = currentEpisodePosition,
                hasPosition = currentEpisodePosition > 0
            )
        }
        sniff()
    }

    private fun sniff() = intent {
        postSideEffect(PlayerIntent.SetAnimeOriginPlayerUrl(state.currentOriginPlayerUrl))
        /*
        mainLaunch {
            if (SPSettings.enableX5 && SPSettings.x5Available) {
                cn.xihan.sniffing.x5.SniffingUtil.get()
                    .activity(topActivity)
                    .url(state.currentOriginPlayerUrl)
                    .referer(state.currentOriginPlayerUrl)
                    .callback(this@PlayerViewModel)
//                .autoRelease(true)
                    .start()
            } else {
                cn.xihan.sniffing.web.SniffingUtil.get()
                    .activity(topActivity)
                    .url(state.currentOriginPlayerUrl)
                    .referer(state.currentOriginPlayerUrl)
                    .callback(this@PlayerViewModel)
//                .autoRelease(true)
                    .start()
            }
        }

         */
    }

    /**
     * 查询当前番剧模型
     */
    private fun queryAnimeModel() = intent {
        localRepository.queryAnimeDetailModelByAnimeId(animeId).collect { animeModel ->
            animeModel.playlists.whatIfNotNullOrEmpty { notNullMap ->
                notNullMap[episodeType]?.map { it[0] }.whatIfNotNullOrEmpty { list ->
                    reduce {
                        state.copy(
                            currentPlayerIndex = list.lastIndexOf(episodeTitle),
                            currentOriginPlayerList = notNullMap[episodeType],
                            currentAnimePlayerList = list,
                            currentAnimeDetailModel = animeModel,
                            currentAnimePlayerType = notNullMap.keys.indexOf(episodeType),
                        )
                    }
                    changePlayerIndex()
                }
            }
        }
    }

    /**
     * 获取Url内容
     */
    fun getContent(url: String) = intent {
        remoteRepository.getContent(url = url).collect { content ->
            if (content.isNotBlank() && content.isWebUrl()) {
                postSideEffect(PlayerIntent.SetPlayerUrl(url))
            }
        }
    }

    /**
     * 保存播放进度
     */
    fun saveHistoryProgress(
        currentPosition: Long = 0L, currentDuration: Long = 0L
    ) = intent {
        state.currentAnimeDetailModel?.let {
            SpUtil("HistoryProgress").encodes {
                removeKey("$animeId/${state.currentEpisodeTitle}")
                encode(
                    "$animeId/${state.currentEpisodeTitle}", currentPosition
                )
            }
            localRepository.updateHistory(
                animeId = animeId,
                animeName = it.name,
                animeCover = it.cover,
                animeLatestPlayTitle = state.currentAnimePlayerList.last(),
                animeLastPlayTitle = state.currentEpisodeTitle,
                animePlayListType = episodeType,
                animeLastPlayProgress = currentPosition,
                animeLastPlayDuration = currentDuration
            )
        }
    }

    override fun onSniffingSuccess(
        webView: View,
        url: String,
        videos: MutableList<SniffingVideo>
    ) {
        intent {
            if (state.currentOriginPlayerUrl == url && videos.isEmpty()) {
                showError(AgeException.ToastException(context.getString(R.string.play_sniffing_failed)))
            } else {
                val urls = videos.map { it.url }
                var url = ""
                if (urls.size == 1) {
                    url = urls.first()
                } else {
                    Utils.showMultiplePlayUrlDialog(
                        playUrls = urls,
                        onPlayUrlSelectListener = {
                            url = it
                        }
                    )
                }
                reduce {
                    state.copy(
                        loading = false
                    )
                }
                if (url.isNotBlank() && url.isWebUrl()) {
                    postSideEffect(PlayerIntent.SetPlayerUrl(url))
                } else {
                    showError(AgeException.ToastException(context.getString(R.string.play_sniffing_failed)))
                }
            }
        }
        logDebug("onSniffingSuccess: $url size:${videos.map { it.url }}")
    }

    override fun onSniffingError(webView: View, url: String, errorCode: Int) {
        showError(AgeException.ToastException(context.getString(R.string.play_sniffing_failed)))
    }

    override fun onSniffingStart(webView: View, url: String) {
        intent {
            reduce { state.copy(loading = true) }
        }
    }

    override fun onSniffingFinish(webView: View, url: String) {
        intent {
            reduce { state.copy(loading = false) }
        }
    }

    override fun showError(error: AgeException) {
        intent {
            reduce { state.copy(error = error) }
        }
    }

    override fun hideError() {
        sniff()
        intent {
            reduce {
                state.copy(error = null)
            }
        }
    }

    fun hideLoading() = intent {
        reduce { state.copy(loading = false) }
    }

    init {
        queryAnimeModel()
        logDebug("init: $animeId $episodeType $episodeTitle")
    }

}


data class PlayerState(
    override var loading: Boolean = true,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val currentPlayerIndex: Int = 0,
    var currentPosition: Long = 0,
    var currentDuration: Long = 0,
    var hasPosition: Boolean = false,
    var hasPreVideo: Boolean = false,
    var hasNextVideo: Boolean = false,
    val currentAnimePlayerType: Int = 0,
    val currentEpisodeTitle: String = "",
    val currentOriginPlayerUrl: String = "",
    val currentOriginPlayerList: List<List<String>>? = emptyList(),
    val currentAnimePlayerList: List<String> = emptyList(),
    val currentAnimeDetailModel: AnimeDetailModel.VideoModel? = null
) : IUiState

sealed class PlayerIntent : IUiIntent {
    data class SetPlayerUrl(
        val url: String
    ) : PlayerIntent()

    data class SetAnimeOriginPlayerUrl(val url: String) : PlayerIntent()
}
