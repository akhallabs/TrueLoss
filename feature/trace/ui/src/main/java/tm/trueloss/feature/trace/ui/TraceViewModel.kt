package tm.trueloss.feature.trace.ui
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tm.trueloss.core.ui.base.BaseViewModel
import tm.trueloss.feature.history.domain.model.HistoryItem
import tm.trueloss.feature.history.domain.repository.HistoryRepository
import tm.trueloss.feature.trace.domain.usecase.StartTraceUseCase
import java.util.UUID
import javax.inject.Inject
@HiltViewModel
class TraceViewModel @Inject constructor(private val startTrace: StartTraceUseCase, private val historyRepo: HistoryRepository) : BaseViewModel<TraceUiState, TraceEvent, TraceEffect>(TraceUiState()) {
    private var job: Job? = null
    override fun onEvent(event: TraceEvent) {
        when (event) {
            is TraceEvent.TargetChanged -> setState { copy(target = event.v, error = null) }
            is TraceEvent.ProtocolChanged -> setState { copy(protocol = event.p) }
            is TraceEvent.IpVersionChanged -> setState { copy(ipVersion = event.v) }
            is TraceEvent.Start -> start()
            is TraceEvent.Stop -> stopAndSave()
            is TraceEvent.Clear -> { job?.cancel(); setState { TraceUiState(target = uiState.value.target, protocol = uiState.value.protocol, ipVersion = uiState.value.ipVersion) } }
        }
    }
    private fun start() {
        val s = uiState.value
        if (!s.isTargetValid) { setState { copy(error = "Geçerli IP veya domain girin") }; return }
        job?.cancel()
        setState { copy(isRunning = true, hops = emptyList(), error = null, avgLoss = 0f, maxLoss = 0f, completedHops = 0) }
        job = viewModelScope.launch {
            try {
                startTrace(s.config).collectLatest { hops ->
                    val avg = if (hops.isEmpty()) 0f else hops.map { it.lossPercent }.average().toFloat()
                    val max = hops.maxOfOrNull { it.lossPercent } ?: 0f
                    setState { copy(hops = hops, avgLoss = avg, maxLoss = max, completedHops = hops.size) }
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                setState { copy(error = e.message ?: "Hata", isRunning = false) }
            }
        }
    }
    private fun stopAndSave() {
        val s = uiState.value
        job?.cancel()
        setState { copy(isRunning = false) }
        if (s.hops.isNotEmpty() && s.target.isNotBlank()) {
            viewModelScope.launch {
                historyRepo.save(HistoryItem(id = UUID.randomUUID().toString(), target = s.target, date = System.currentTimeMillis(), hopCount = s.hops.size, avgLoss = s.avgLoss))
            }
        }
    }
}
