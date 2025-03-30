package dev.goood.chat_client.services

import com.russhwolf.settings.Settings
import dev.goood.chat_client.model.User

class LocalStorage {
    private val settings: Settings = Settings()
    private object PreferenceKeys {
        const val USER_NAME = "UserName"
        const val USER_PASSWORD = "UserPass"
        const val BEARER_TOKEN = "BearerToken"
    }

    var user: User?
        get() {
            val name = settings.getStringOrNull(PreferenceKeys.USER_NAME)
            val password = settings.getStringOrNull(PreferenceKeys.USER_PASSWORD)
            return if (name != null && password != null) {
                User(name, password)
            } else {
                null
            }
        }
        set(value) {
            if (value == null) {
                settings.remove(PreferenceKeys.USER_NAME)
                settings.remove(PreferenceKeys.USER_PASSWORD)
            } else {
                settings.putString(PreferenceKeys.USER_NAME, value.email)
                settings.putString(PreferenceKeys.USER_PASSWORD, value.password)
            }
        }

    var bearerToken: String?
        get() = settings.getStringOrNull(PreferenceKeys.BEARER_TOKEN)
        set(value) {
            if (value == null) {
                settings.remove(PreferenceKeys.BEARER_TOKEN)
            } else {
                settings.putString(PreferenceKeys.BEARER_TOKEN, value)
            }
        }
}