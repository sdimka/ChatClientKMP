package dev.goood.chat_client.di

import dev.goood.chat_client.cache.AndroidDatabaseDriverFactory
import dev.goood.chat_client.cache.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(context = get()) }
    }