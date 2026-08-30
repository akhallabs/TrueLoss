package tm.trueloss.feature.trace.ui
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tm.trueloss.core.designsystem.theme.LossHigh
import tm.trueloss.core.designsystem.theme.LossLow
import tm.trueloss.core.designsystem.theme.LossMedium
import tm.trueloss.core.designsystem.theme.LossNone
import tm.trueloss.feature.trace.domain.model.IpVersion
import tm.trueloss.feature.trace.domain.model.TraceProtocol
import kotlin.math.roundToInt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceScreen(vm: TraceViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Explore, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("TrueLoss", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface, scrolledContainerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { InputCard(state, vm) }
            if (state.error != null) {
                item { Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) { Text(state.error!!, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium) } }
            }
            if (state.hops.isNotEmpty() || state.isRunning) {
                item { SummaryHero(state) }
                item { RttGraphCard(hops = state.hops) }
            }
            itemsIndexed(state.hops) { index, hop -> HopRow(index = hop.hop, ip = hop.ip, hostname = hop.hostname, rtts = hop.rttList, loss = hop.lossPercent, asn = hop.asn, country = hop.country, city = hop.city) }
            if (state.hops.isEmpty() && !state.isRunning) { item { EmptyState() } }
        }
    }
}
@Composable
private fun InputCard(state: TraceUiState, vm: TraceViewModel) {
    ElevatedCard(shape = MaterialTheme.shapes.extraLarge, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Hedef", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = state.target, onValueChange = { vm.onEvent(TraceEvent.TargetChanged(it)) }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("8.8.8.8  veya  example.com") }, leadingIcon = { Icon(Icons.Default.Search, null) }, trailingIcon = { if (state.target.isNotEmpty()) IconButton(onClick = { vm.onEvent(TraceEvent.TargetChanged("")) }) { Icon(Icons.Default.Close, null) } }, singleLine = true, shape = MaterialTheme.shapes.large, supportingText = { if (!state.isTargetValid && state.target.isNotEmpty()) Text("En az 3 karakter") })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = state.protocol == TraceProtocol.ICMP, onClick = { vm.onEvent(TraceEvent.ProtocolChanged(TraceProtocol.ICMP)) }, label = { Text("ICMP") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer))
                FilterChip(selected = state.protocol == TraceProtocol.UDP, onClick = { vm.onEvent(TraceEvent.ProtocolChanged(TraceProtocol.UDP)) }, label = { Text("UDP") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer))
                FilterChip(selected = state.protocol == TraceProtocol.TCP, onClick = { vm.onEvent(TraceEvent.ProtocolChanged(TraceProtocol.TCP)) }, label = { Text("TCP") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = state.ipVersion == IpVersion.IPv4, onClick = { vm.onEvent(TraceEvent.IpVersionChanged(IpVersion.IPv4)) }, label = { Text("IPv4") })
                FilterChip(selected = state.ipVersion == IpVersion.IPv6, onClick = { vm.onEvent(TraceEvent.IpVersionChanged(IpVersion.IPv6)) }, label = { Text("IPv6") })
                AssistChip(onClick = {}, label = { Text(state.protocol.name + " • " + state.ipVersion.name) }, leadingIcon = { Icon(Icons.Default.Explore, null, Modifier.size(16.dp)) }, colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                if (state.isRunning) {
                    FilledTonalButton(onClick = { vm.onEvent(TraceEvent.Stop) }, modifier = Modifier.weight(1f).height(52.dp), shape = MaterialTheme.shapes.large) { Icon(Icons.Default.Pause, null); Spacer(Modifier.width(8.dp)); Text("Durdur") }
                    LinearProgressIndicator(modifier = Modifier.align(Alignment.CenterVertically).weight(0.5f))
                } else {
                    androidx.compose.material3.Button(onClick = { vm.onEvent(TraceEvent.Start) }, enabled = state.isTargetValid, modifier = Modifier.weight(1f).height(52.dp), shape = MaterialTheme.shapes.large) { Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text("Loss Kontrol Et", style = MaterialTheme.typography.labelLarge) }
                    FilledTonalButton(onClick = { vm.onEvent(TraceEvent.Clear) }, modifier = Modifier.height(52.dp), shape = MaterialTheme.shapes.large) { Text("Temizle") }
                }
            }
        }
    }
}
@Composable
private fun SummaryHero(state: TraceUiState) {
    val animatedLoss by animateFloatAsState(targetValue = state.avgLoss / 100f, label = "loss")
    Card(shape = MaterialTheme.shapes.extraLarge, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(92.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(color = Color.White.copy(alpha = 0.35f), style = Stroke(width = 10f, cap = StrokeCap.Round))
                    val sweep = 360f * animatedLoss
                    val lossColor = when { state.avgLoss == 0f -> LossNone; state.avgLoss < 2f -> LossLow; state.avgLoss < 10f -> LossMedium; else -> LossHigh }
                    drawArc(color = lossColor, startAngle = -90f, sweepAngle = sweep, useCenter = false, style = Stroke(width = 10f, cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("${state.avgLoss.roundToInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer); Text("Ort. Loss", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer) }
            }
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Text(if (state.avgLoss == 0f) "Kayıp yok" else if (state.avgLoss < 2f) "İyi" else if (state.avgLoss < 10f) "Orta" else "Yüksek kayıp", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("${state.completedHops} hop • Max %${state.maxLoss.roundToInt()} • ${state.protocol.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                LinearProgressIndicator(progress = { animatedLoss }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape), color = when { state.avgLoss == 0f -> LossNone; state.avgLoss < 2f -> LossLow; state.avgLoss < 10f -> LossMedium; else -> LossHigh }, trackColor = Color.White.copy(alpha = 0.4f))
            }
        }
    }
}
@Composable
private fun RttGraphCard(hops: List<tm.trueloss.feature.trace.domain.model.TraceHop>) {
    if (hops.isEmpty()) return
    val rtts = hops.mapNotNull { it.rttList.minOrNull() }
    if (rtts.isEmpty()) return
    ElevatedCard(shape = MaterialTheme.shapes.large, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(16.dp)) {
            Text("RTT Grafiği", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            Canvas(Modifier.fillMaxWidth().height(80.dp).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceContainerHighest)) {
                if (rtts.size < 2) return@Canvas
                val max = rtts.maxOrNull() ?: 1f
                val min = rtts.minOrNull() ?: 0f
                val range = (max - min).coerceAtLeast(1f)
                val stepX = size.width / (rtts.size - 1).coerceAtLeast(1)
                val path = Path()
                val fillPath = Path()
                rtts.forEachIndexed { i, v ->
                    val x = i * stepX
                    val y = size.height - ((v - min) / range * (size.height - 16.dp.toPx()) + 8.dp.toPx())
                    if (i == 0) { path.moveTo(x, y); fillPath.moveTo(x, size.height); fillPath.lineTo(x, y) } else { path.lineTo(x, y); fillPath.lineTo(x, y) }
                }
                fillPath.lineTo((rtts.size - 1) * stepX, size.height); fillPath.close()
                drawPath(fillPath, Color(0xFF0A84FF).copy(alpha = 0.15f))
                drawPath(path, Color(0xFF0A84FF), style = Stroke(width = 3f, cap = StrokeCap.Round))
                rtts.forEachIndexed { i, v ->
                    val x = i * stepX
                    val y = size.height - ((v - min) / range * (size.height - 16.dp.toPx()) + 8.dp.toPx())
                    drawCircle(Color(0xFF0A84FF), radius = 4.dp.toPx(), center = Offset(x, y))
                    drawCircle(Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${rtts.minOrNull()?.roundToInt()} ms", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("${rtts.maxOrNull()?.roundToInt()} ms max", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
@Composable
private fun HopRow(index: Int, ip: String?, hostname: String?, rtts: List<Float>, loss: Float, asn: String?, country: String?, city: String?) {
    val lossColor = when { loss == 0f -> LossNone; loss < 2f -> LossLow; loss < 10f -> LossMedium; loss >= 10f -> LossHigh; else -> LossHigh }
    val bg = if (loss == 0f) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = bg), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(lossColor.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) { Text("$index", style = MaterialTheme.typography.labelLarge, color = lossColor, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) { Text(ip ?: "—", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold); if (hostname != null) Text(hostname, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (rtts.isNotEmpty()) { Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { rtts.take(3).forEach { r -> Text("${r.roundToInt()} ms", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { if (asn != null) Text(asn, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary); if (country != null) Text(listOfNotNull(country, city).joinToString(" / "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Column(horizontalAlignment = Alignment.End) { Text(if (loss == 100f) "Timeout" else "%${loss.roundToInt()}", style = MaterialTheme.typography.labelLarge, color = lossColor, fontWeight = FontWeight.Bold); Text(if (loss == 0f) "Kayıpsız" else if (loss < 1f) "İyi" else "Kayıp", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
@Composable
private fun EmptyState() {
    Card(shape = MaterialTheme.shapes.extraLarge, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Explore, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Text("Loss kontrolüne hazır", style = MaterialTheme.typography.titleMedium)
            Text("Domain veya IP yaz ve kontrol et. Gerçek ICMP/UDP/TCP traceroute ile her hop için loss ve RTT ölçülür.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
@Composable fun TraceRoute(onNavigateToResult: (String) -> Unit = {}) { TraceScreen() }
