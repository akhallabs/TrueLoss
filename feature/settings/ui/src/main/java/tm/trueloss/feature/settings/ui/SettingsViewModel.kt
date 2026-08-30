package tm.trueloss.feature.settings.ui
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import tm.trueloss.core.datastore.PreferencesManager
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(private val prefs: PreferencesManager) : ViewModel() {
    val theme: Flow<String> = prefs.themeFlow
    suspend fun setTheme(v: String) = prefs.setTheme(v)
}
