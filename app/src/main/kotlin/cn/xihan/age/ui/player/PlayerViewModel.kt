package cn.xihan.age.ui.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import cn.xihan.age.R
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.SharedPreferencesUtil
import cn.xihan.age.util.Utils.getEpModelByIndex
import cn.xihan.age.util.Utils.getEpNameIndex
import cn.xihan.age.util.Utils.getNewListByPlayModel
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.isNotWebUrl
import cn.xihan.age.util.extension.logDebug
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
) : BaseViewModel<PlayerState, IUiIntent>() {

    override fun initViewState(): PlayerState = PlayerState()

    val animeId: String = checkNotNull(savedStateHandle["animeId"])
    var animeTitle: String = ""
    var animeCover: String = ""
    val episodeId: Int = checkNotNull(savedStateHandle["episodeId"])
    val episodeName: String = checkNotNull(savedStateHandle["episodeName"])

    fun queryAnimeDetailModel() = intent {
        localRepository.queryAnimeDetailModelByAnimeId(animeId).collect {
            animeTitle = it.r动画名称
            animeCover = it.r封面图
            it.r在线播放All.whatIfNotNullOrEmpty(
                whatIf = { list ->
                    list[episodeId].whatIfNotNullOrEmpty { list ->
                        if (episodeName.isNotBlank()) {
                            list.find { it1 -> it1.epName == episodeName || it1.title == episodeName }
                                ?.let { it2 ->
                                    val currentPosition =
                                        SharedPreferencesUtil("HistoryProgress").decodeLong(
                                            animeId + it2.title,
                                            0
                                        )
                                    if (it2.playVid.isNotWebUrl()) {
                                        it2.playVid = remoteRepository.getAnimePlayUrl(it2.playVid)
                                    }
                                    postSideEffect(
                                        PlayerIntent.SetSelectedVideoModel(
                                            it2.copy(
                                                playing = true
                                            )
                                        )
                                    )
                                    reduce {
                                        state.copy(
                                            currentPlayerPosition = list.getEpNameIndex(it2.epName),
                                            playerList = list.getNewListByPlayModel(it2),
                                            currentPosition = currentPosition,
                                            hasPosition = currentPosition > 0
                                        )
                                    }
                                }
                        }
                    }
                },
                whatIfNot = {
                    throw AgeException.SnackBarException(context.getString(R.string.error_empty))
                }
            )
        }
    }

    fun setSelectedVideoPosition(
        targetIndex: Int = -1,
        isNext: Boolean = false,
        playModel: AnimeDetailModel.AniInfoModel.PlayModel? = null
    ) = intent {
        if (playModel == null) {
            val targetModel = if (targetIndex == -1) {
                state.playerList.getEpModelByIndex(state.currentPlayerPosition, isNext)
            } else {
                state.playerList.getEpModelByIndex(targetIndex, isNext)
            }
            targetModel?.let {
                val currentPosition =
                    SharedPreferencesUtil("HistoryProgress").decodeLong(animeId + it.title, 0)
                if (it.playVid.isNotWebUrl()) {
                    it.playVid = remoteRepository.getAnimePlayUrl(it.playVid)
                }
                postSideEffect(PlayerIntent.SetSelectedVideoModel(it))
                reduce {
                    state.copy(
                        currentPlayerPosition = state.playerList.getEpNameIndex(targetModel.epName),
                        playerList = state.playerList.getNewListByPlayModel(it),
                        currentPosition = currentPosition,
                        hasPosition = currentPosition > 0
                    )
                }
            }
        } else {
            val currentPosition =
                SharedPreferencesUtil("HistoryProgress").decodeLong(animeId + playModel.title, 0)
            if (playModel.playVid.isNotWebUrl()) {
                playModel.playVid = remoteRepository.getAnimePlayUrl(playModel.playVid)
            }
            postSideEffect(PlayerIntent.SetSelectedVideoModel(playModel.copy(playing = true)))
            reduce {
                state.copy(
                    currentPlayerPosition = state.playerList.getEpNameIndex(playModel.epName),
                    playerList = state.playerList.getNewListByPlayModel(playModel),
                    currentPosition = currentPosition,
                    hasPosition = currentPosition > 0
                )
            }
        }

    }

    /**
     * 保存视频播放记录
     * @param [animeId] 番剧ID
     * @param [episodeId] 集数ID
     * @param [episodeName] 集数名称
     * @param [list] 视频列表
     * @param [currentPosition] 当前播放位置
     */
    fun saveVideoPlayRecord(
        currentPosition: Long,
        currentDuration: Long
    ) = intent {
        logDebug("saveVideoPlayRecord")
        val currentPlayerTitle = state.playerList[state.currentPlayerPosition].title
        if (currentPlayerTitle.isNotBlank()) {
            localRepository.updateHistory(
                animeId = animeId,
                animeName = animeTitle,
                animeCover = animeCover,
                animeLatestPlayTitle = state.playerList.last().title,
                animeLastPlayTitle = currentPlayerTitle,
                animeLastPlayProgress = currentPosition,
                animeLastPlayDuration = currentDuration,
                animePlayListIndex = episodeId,
            )
            SharedPreferencesUtil("HistoryProgress").encode(
                animeId + currentPlayerTitle,
                currentPosition
            )
        }
    }

    init {
        queryAnimeDetailModel()
    }

}


data class PlayerState(
    override var loading: Boolean = true,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val playerList: List<AnimeDetailModel.AniInfoModel.PlayModel> = emptyList(),
//    var mediaItemList: List<VideoPlayerMediaItem.NetworkMediaItem> = emptyList(),
    val currentPlayerPosition: Int = 0,
    val currentPosition: Long = 0,
    var hasPosition: Boolean = false
) : IUiState


sealed class PlayerIntent : IUiIntent {

    data class SetSelectedVideoModel(
        val selectedVideoModel: AnimeDetailModel.AniInfoModel.PlayModel
    ) : PlayerIntent()
}