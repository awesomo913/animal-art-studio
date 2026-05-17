package com.animalartstudio.kids.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * REVIEW_NOTES C-8 (lite).
 *
 * Persisted parent-controlled toggles. DataStore-backed so it survives across
 * process death without the SharedPreferences quirks (synchronous writes, no
 * coroutine boundary).
 *
 * Today only stores `muteSounds` + `maxSessionMinutes` + `offlineOnly`. Add to
 * [ParentSettings] when new toggles arrive; defaults live there.
 */
data class ParentSettings(
    val muteSounds: Boolean = false,
    val maxSessionMinutes: Int = 20,
    val offlineOnly: Boolean = false,
) {
  companion object {
    val DEFAULT = ParentSettings()
    /** Sentinel for "no cap". UI maps this to "Until the kid quits". */
    const val NO_SESSION_CAP = 0
  }
}

private val Context.parentSettingsStore: DataStore<Preferences> by preferencesDataStore(name = "parent_settings")

class ParentSettingsRepo(private val ctx: Context) {

  private object Keys {
    val MUTE = booleanPreferencesKey("mute_sounds")
    val MAX_MIN = intPreferencesKey("max_session_minutes")
    val OFFLINE = booleanPreferencesKey("offline_only")
  }

  val settings: Flow<ParentSettings> =
      ctx.parentSettingsStore.data.map { p ->
        ParentSettings(
            muteSounds = p[Keys.MUTE] ?: ParentSettings.DEFAULT.muteSounds,
            maxSessionMinutes = p[Keys.MAX_MIN] ?: ParentSettings.DEFAULT.maxSessionMinutes,
            offlineOnly = p[Keys.OFFLINE] ?: ParentSettings.DEFAULT.offlineOnly,
        )
      }

  suspend fun setMute(value: Boolean) {
    ctx.parentSettingsStore.edit { it[Keys.MUTE] = value }
  }

  suspend fun setMaxSessionMinutes(value: Int) {
    ctx.parentSettingsStore.edit { it[Keys.MAX_MIN] = value }
  }

  suspend fun setOfflineOnly(value: Boolean) {
    ctx.parentSettingsStore.edit { it[Keys.OFFLINE] = value }
  }
}
