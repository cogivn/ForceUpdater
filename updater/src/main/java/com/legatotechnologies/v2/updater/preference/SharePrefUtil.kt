package com.legatotechnologies.v2.updater.preference

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.WorkerThread
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import java.io.IOException
import java.security.GeneralSecurityException


internal class SharePrefUtil(context: Context, config: Config) {
    var mSharePreference: SharedPreferences? = null
        private set

    init {
        try {
            val alias = MasterKey.DEFAULT_MASTER_KEY_ALIAS
            val purpose = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val spec = KeyGenParameterSpec.Builder(alias, purpose)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            val masterKey = MasterKey.Builder(context)
                .setKeyGenParameterSpec(spec)
                .build()

            mSharePreference = if (config.isSecure) EncryptedSharedPreferences.create(
                context,
                config.preferenceName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ) else context.getSharedPreferences(config.preferenceName, config.preferenceMode)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Save int value into share pref
     *
     * @param name  the key
     * @param value the value map to the key
     */
    fun saveInt(name: String?, value: Int) {
        mSharePreference?.apply {
            val editor = edit()
            editor.putInt(name, value)
            editor.apply()
        }
    }

    /**
     * Save float value into share pref
     *
     * @param name  the key
     * @param value the value map to the key
     */
    fun saveFloat(name: String?, value: Float) {
        mSharePreference?.apply {
            val editor = edit()
            editor.putFloat(name, value)
            editor.apply()
        }
    }

    /**
     * Save String value into share pref
     *
     * @param name  the key
     * @param value the value map to the key
     */
    fun saveString(name: String?, value: String?) {
        mSharePreference?.apply {
            val editor = edit()
            editor.putString(name, value)
            editor.apply()
        }
    }

    /**
     * Save Object value into share pref
     *
     * @param name  the key
     * @param value the value map to the key
     */
    @WorkerThread
    fun saveObject(name: String?, value: Any?) {
        if (value == null) return
        mSharePreference?.apply {
            val editor = edit()
            val json = try {
                Gson().toJson(value)
            } catch (ex: Exception) {
                "{}"
            }
            editor.putString(name, json)
            editor.apply()
        }
    }

    /**
     * Save boolean into share pref
     *
     * @param name  the key
     * @param value the value map to the key
     */
    fun saveBoolean(name: String?, value: Boolean) {
        mSharePreference?.apply {
            val editor = edit()
            editor.putBoolean(name, value)
            editor.apply()
        }
    }

    /**
     * Save Long into share pref
     *
     * @param name  the key
     * @param value the value map to the key
     */
    fun saveLong(name: String?, value: Long) {
        mSharePreference?.apply {
            val editor = edit()
            editor.putLong(name, value)
            editor.apply()
        }
    }

    /**
     * Read value from temp share pref
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readString(name: String?, def: String): String {
        return mSharePreference?.getString(name, def) ?: def
    }

    /**
     * read Object value from share pref
     *
     * @param name the key
     * @param type type of object you want to parse
     */
    @WorkerThread
    inline fun <reified T> readObject(name: String?): T? {
        return mSharePreference?.let {
            val json = it.getString(name, null)
            return try {
                Gson().fromJson(json, T::class.java)
            } catch (ex: Exception) {
                null
            }
        }
    }

    /**
     * Read value from share pref
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readBoolean(name: String?, def: Boolean): Boolean {
        return mSharePreference?.getBoolean(name, def) ?: def
    }

    /**
     * Read value from share pref
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readFloat(name: String?, def: Float): Float {
        return mSharePreference?.getFloat(name, def) ?: def
    }

    /**
     * Read value from share pref
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readInt(name: String?, def: Int): Int {
        return mSharePreference?.getInt(name, def) ?: def
    }

    /**
     * Read value from share pref
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readLong(name: String?, def: Long): Long {
        return mSharePreference?.getLong(name, def) ?: def
    }

    /**
     * Read value from temp share pref
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readBooleanTemp(name: String?, def: Boolean): Boolean {
        return mSharePreference?.let {
            val value = it.getBoolean(name, def)
            val editor = it.edit()
            editor.remove(name)
            editor.apply()
            return value
        } ?: def
    }

    /**
     * Read and delete value from temp share preferences
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readIntTemp(name: String?, def: Int): Int {
        return mSharePreference?.let {
            val value = it.getInt(name, def)
            val editor = it.edit()
            editor.remove(name)
            editor.apply()
            return value
        } ?: def
    }

    /**
     * Read and delete value from temp share preferences
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readStringTemp(name: String?, def: String): String {
        return mSharePreference?.let {
            val value = it.getString(name, def)
            val editor = it.edit()
            editor.remove(name)
            editor.apply()
            return value ?: def
        } ?: def
    }

    /**
     * Read and delete value from temp share preferences
     *
     * @param name the key
     * @param def  the default value when the key is invalid
     * @return the value map to the key
     */
    fun readLongTemp(name: String?, def: Long): Long {
        return mSharePreference?.let {
            val value = it.getLong(name, def)
            val editor = it.edit()
            editor.remove(name)
            editor.apply()
            return value
        } ?: def
    }

    /**
     * remove key from share pref
     *
     * @param name the key
     * @return true if removed, false otherwise
     */
    fun removeKey(name: String?): Boolean {
        return mSharePreference?.let {
            val editor = it.edit()
            editor.remove(name)
            editor.apply()
            return true
        } ?: false
    }

    class Builder(private val context: Context) {
        private var name: String = "default.share_pref"
        private val config: Config = Config(name)

        fun setName(name: String) = apply { config.preferenceName = name }
        fun setSecure(isSecure: Boolean) = apply { config.isSecure = isSecure }
        fun setPreferenceMode(mode: Int) = apply { config.preferenceMode = mode }
        fun ok() = SharePrefUtil(context, config)
    }

    companion object {
        fun with(context: Context) = Builder(context)
    }
}