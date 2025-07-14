package dev.goood.chat_client.di

import dev.goood.chat_client.cache.DatabaseDriverFactory
import dev.goood.chat_client.cache.IOSDatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module { single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() } }