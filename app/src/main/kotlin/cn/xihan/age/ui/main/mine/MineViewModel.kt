package cn.xihan.age.ui.main.mine

import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.network.SPSettings
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.util.extension.AgeException
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/12 8:51
 * @介绍 :
 */
@HiltViewModel
class MineViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : BaseViewModel<MineState, IUiIntent>() {

    override fun initViewState(): MineState = MineState()

}

data class MineState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val isFollowVisible: Boolean = false
) : IUiState