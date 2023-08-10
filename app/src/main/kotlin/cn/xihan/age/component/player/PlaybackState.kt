package cn.xihan.age.component.player

import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM


/**
 * 播放状态
 * @author MissYang
 * @date 2023/04/06
 * @constructor 创建[PlaybackState]
 * @param [value] 价值
 */
enum class PlaybackState(val value: Int) {

    IDLE(STATE_IDLE),
    BUFFERING(STATE_BUFFERING),
    READY(STATE_READY),
    ENDED(STATE_ENDED);

    companion object {
        fun of(value: Int): PlaybackState {
            return values().first { it.value == value }
        }
    }
}

/**
 * 播放显示模式
 * @author MissYang
 * @date 2023/04/07
 * @constructor 创建[PlayDisplayMode]
 * @param [value] 价值
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
enum class PlayDisplayMode(val value: Int) {

    FIT(RESIZE_MODE_FIT),
    FIXED_WIDTH(RESIZE_MODE_FIXED_WIDTH),
    FIXED_HEIGHT(RESIZE_MODE_FIXED_HEIGHT),
    FILL(RESIZE_MODE_FILL),
    ZOOM(RESIZE_MODE_ZOOM);

    companion object {
        fun of(value: Int): PlayDisplayMode {
            return values().first { it.value == value }
        }
    }

}

/**
 * 播放速度
 * @author MissYang
 * @date 2023/04/07
 * @constructor 创建[PlaySpeed]
 * @param [value] 值
 */
enum class PlaySpeed(val value: Float) {
    SPEED_0_5(0.5f),
    SPEED_1(1f),
    SPEED_1_5(1.5f),
    SPEED_2(2.0f),
    SPEED_2_5(2.5f),
    SPEED_3(3.0f);


    companion object {
        fun of(value: Float): PlaySpeed {
            return values().first { it.value == value }
        }

        fun of(index: Int): Float {
            return values()[index].value
        }

        fun indexOf(value: Float): Int {
            return values().indexOfFirst { it.value == value }
        }
    }
}