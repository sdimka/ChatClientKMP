package dev.goood.chat_client.viewModels

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    fun auth(email: String, password: String) {}

    fun getData(): String {
        return "Data from ViewModel"
    }
}