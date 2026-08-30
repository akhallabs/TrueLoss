package tm.trueloss.core.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<State, Event, Effect>(initialState: State) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()
    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    protected fun setState(reducer: State.() -> State) { _uiState.value = _uiState.value.reducer() }
    protected suspend fun sendEffect(effect: Effect) { _effect.send(effect) }
    abstract fun onEvent(event: Event)
}
