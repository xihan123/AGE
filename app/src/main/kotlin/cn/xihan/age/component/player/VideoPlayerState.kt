package cn.xihan.age.component.player

import android.os.Parcelable
import androidx.annotation.Keep
import cn.xihan.age.util.extension.AgeException
import kotlinx.parcelize.Parcelize


/**
 * 视频播放器状态
 * @author MissYang
 * @date 2023/04/07
 * @constructor 创建[VideoPlayerState]
 * @param [isPlaying] 是播放中
 * @param [controlsVisible] 控件可见
 * @param [controlsEnabled] 控件启用
 * @param [gesturesEnabled] 手势启用
 * @param [duration] 持续时间
 * @param [currentPosition] 当前位置
 * @param [secondaryProgress] 二次发展
 * @param [speed] 速度
 * @param [volume] 音量
 * @param [luminance] 亮度
 * @param [videoSize] 视频大小
 * @param [draggingProgress] 拖着进度
 * @param [playbackState] 播放状态
 * @param [playDisplayMode] 播放显示模式
 * @param [quickSeekAction] 快进快退动作
 */
@Parcelize
@Keep
data class VideoPlayerState(
    val isPlaying: Boolean = true,
    var controlsVisible: Boolean = true,
    var controlsEnabled: Boolean = true,
    val gesturesEnabled: Boolean = true,
    var hideBottomProgress: Boolean = false,
    val playReady: Boolean = false,
    val playEnded: Boolean = false,
    val duration: Long = 1L,
    val bufferedPosition: Long = 1L,
    val currentPosition: Long = 1L,
    val secondaryProgress: Long = 1L,
    val speed: Float = 1f,
    val volume: Float = 1f,
    val luminance: Float = 1f,
    val videoSize: Pair<Float, Float> = 1920f to 1080f,
    val draggingProgress: DraggingProgress? = null,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val playDisplayMode: PlayDisplayMode = PlayDisplayMode.FILL,
    val quickSeekAction: QuickSeekAction = QuickSeekAction.none(),
    val seekDirection: VideoSeekDirection = VideoSeekDirection.NONE,
    var error: AgeException? = null
) : Parcelable