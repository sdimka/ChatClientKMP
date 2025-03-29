package dev.goood.chat_client.di

import org.koin.core.module.dsl.bind
import dev.goood.chat_client.getPlatform
import dev.goood.chat_client.services.AuthService
import dev.goood.chat_client.services.AuthServiceImpl
import dev.goood.chat_client.viewModels.LoginViewModel
import dev.goood.chat_client.viewModels.MainViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf


val appModule = module {
    singleOf(::AuthServiceImpl) { bind<AuthService>() }
    viewModelOf(::MainViewModel)
    viewModelOf(::LoginViewModel)
    factory { getPlatform() }
}