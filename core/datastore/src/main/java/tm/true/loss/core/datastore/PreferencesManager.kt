package tm.true.loss.core.datastore
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
private val Context.dataStore by preferencesDataStore(name = "trueloss_prefs")
@Singleton class PreferencesManager @Inject constructor(@ApplicationContext private val ctx: Context){
    val themeFlow = ctx.dataStore.data.map { it[stringPreferencesKey("theme")] ?: "system" }
    suspend fun setTheme(v: String){ ctx.dataStore.edit { it[stringPreferencesKey("theme")] = v } }
}
