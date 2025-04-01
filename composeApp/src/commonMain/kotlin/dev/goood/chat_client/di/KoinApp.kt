package dev.goood.chat_client.di


import org.koin.dsl.KoinConfiguration


fun createKoinConfiguration(): KoinConfiguration {
    return KoinConfiguration() {
        modules(appModule)
    }
}