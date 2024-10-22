package com.flyfish233.lain.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Suppress("unused")
object SpHelper {
    private const val SETTINGS = "settings"

    private fun getPreferences(
        context: Context, name: String = SETTINGS,
    ): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    // ------------------------
    // Check if a Key Exists
    // ------------------------

    fun checkKeyExists(
        context: Context,
        key: String,
        name: String = SETTINGS,
    ): Boolean {
        val prefs = getPreferences(context, name)
        return prefs.contains(key)
    }

    @Composable
    fun checkKeyExists(key: String, name: String = SETTINGS): Boolean =
        checkKeyExists(LocalContext.current, key, name)

    // ------------------------
    // Read a Key's Value
    // ------------------------

    @Suppress("UNCHECKED_CAST")
    fun <T> readKeyValue(
        context: Context,
        key: String,
        defaultValue: T,
        name: String = SETTINGS,
    ): T {
        val prefs = getPreferences(context, name)
        return when (defaultValue) {
            is String -> prefs.getString(key, defaultValue) as T
            is Int -> prefs.getInt(key, defaultValue) as T
            is Boolean -> prefs.getBoolean(key, defaultValue) as T
            is Float -> prefs.getFloat(key, defaultValue) as T
            is Long -> prefs.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type.")
        }
    }

    @Composable
    fun <T> readKeyValue(
        key: String,
        defaultValue: T,
        name: String = SETTINGS,
    ): T = readKeyValue(LocalContext.current, key, defaultValue, name)

    // ------------------------
    // Write a Key's Value
    // ------------------------

    fun <T> writeKeyValue(
        context: Context,
        key: String,
        value: T,
        name: String = SETTINGS,
    ) {
        val prefs = getPreferences(context, name)
        with(prefs.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type.")
            }
            apply()
        }
    }

    @Composable
    fun <T> WriteKeyValue(key: String, value: T, name: String = SETTINGS) =
        writeKeyValue(LocalContext.current, key, value, name)

    // ------------------------
    // Listen for Key Changes
    // ------------------------

    // Keep track of registered listeners to prevent duplicates
    // Include name in listener mapping
    private val listeners =
        mutableMapOf<String, MutableMap<SharedPreferences.OnSharedPreferenceChangeListener, SharedPreferences>>()

    fun registerListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
        name: String = SETTINGS,
    ) {
        val prefs = getPreferences(context, name)
        prefs.registerOnSharedPreferenceChangeListener(listener)
        val prefsListeners = listeners.getOrPut(name) { mutableMapOf() }
        prefsListeners[listener] = prefs
    }

    fun unregisterListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
        name: String = SETTINGS,
    ) {
        val prefsListeners = listeners[name]
        val prefs = prefsListeners?.remove(listener)
        prefs?.unregisterOnSharedPreferenceChangeListener(listener)
        if (prefsListeners?.isEmpty() == true) {
            listeners.remove(name)
        }
    }

    @Composable
    fun RegisterListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener,
        name: String = SETTINGS,
    ) = registerListener(LocalContext.current, listener, name)
}

/**
 *  Usage:
 *  fun prefs(context: Context) {
 *     val key = "my_key"
 *     val defaultValue = 0
 *
 *     // Check if key exists
 *     val exists = SharedPreferenceHelper.checkKeyExists(context, key)
 *
 *     // Read a value
 *     val value: Int = SharedPreferenceHelper.readKeyValue(context, key, defaultValue)
 *
 *     // Write a value
 *     SharedPreferenceHelper.writeKeyValue(context, key, 42)
 *
 *     // Register a listener
 *     val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, changedKey ->
 *         if (changedKey == key) {
 *             // Handle the change
 *         }
 *     }
 *     SharedPreferenceHelper.registerListener(context, listener)
 *
 *     // Remember to unregister the listener when appropriate
 *     // For example, when the activity is destroyed:
 *     // SharedPreferenceHelper.unregisterListener(context, listener)
 * }
 *
 */

/**
 * Usage:
 * @Composable
 * fun Prefs() {
 *     val key = "my_key"
 *     val defaultValue = "default"
 *
 *     // Check if key exists
 *     val exists = SharedPreferenceHelper.CheckKeyExists(key)
 *
 *     // Read a value
 *     val value: String = SharedPreferenceHelper.ReadKeyValue(key, defaultValue)
 *
 *     // Write a value
 *     SharedPreferenceHelper.WriteKeyValue(key, "new_value")
 *
 *     // Register a listener
 *     val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, changedKey ->
 *         if (changedKey == key) {
 *             // Handle the change
 *         }
 *     }
 *     SharedPreferenceHelper.RegisterListener(listener)
 *
 *     // Remember to unregister the listener when appropriate
 *     // For example, in a DisposableEffect:
 *     DisposableEffect(Unit) {
 *         onDispose {
 *             SharedPreferenceHelper.UnregisterListener(listener)
 *         }
 *     }
 * }
 *
 */