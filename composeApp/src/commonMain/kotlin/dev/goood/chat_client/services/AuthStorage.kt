package dev.goood.chat_client.services

import dev.goood.chat_client.model.User

class AuthStorage {

    var token: String? = null
        get() {
            return field
        }
        set(value) {
            field = value
        }

    var user: User? = null

}