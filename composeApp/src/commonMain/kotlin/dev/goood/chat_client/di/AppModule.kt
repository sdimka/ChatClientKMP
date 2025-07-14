package dev.goood.chat_client.di

import dev.goood.chat_client.AppViewModel
import dev.goood.chat_client.core.network.Api
import org.koin.core.module.dsl.bind
import dev.goood.chat_client.getPlatform
import dev.goood.chat_client.services.AuthService
import dev.goood.chat_client.services.AuthServiceImpl
import dev.goood.chat_client.services.LocalStorage
import dev.goood.chat_client.services.SystemMessagesService
import dev.goood.chat_client.viewModels.LoginViewModel
import dev.goood.chat_client.viewModels.MainViewModel
import dev.goood.chat_client.viewModels.MainViewModelImpl
import dev.goood.chat_client.viewModels.MainViewModelPreview
import dev.goood.chat_client.viewModels.AddChatViewModel
import dev.goood.chat_client.viewModels.ChatViewModel
import dev.goood.chat_client.viewModels.ChatViewModelImpl
import dev.goood.chat_client.viewModels.ChatViewModelPreview
import dev.goood.chat_client.viewModels.FileDialogViewModel
import dev.goood.chat_client.viewModels.FileDialogViewModelImpl
import dev.goood.chat_client.viewModels.FileDialogViewModelPreview
import dev.goood.chat_client.viewModels.SMDetailViewModel
import dev.goood.chat_client.viewModels.SMDetailViewModelImpl
import dev.goood.chat_client.viewModels.SMDetailViewModelPreview
import dev.goood.chat_client.viewModels.SystemMessagesViewModel
import dev.goood.chat_client.viewModels.SystemMessagesViewModelImpl
import dev.goood.chat_client.viewModels.SystemMessagesViewModelPreview
import dev.goood.chat_client.viewModels.TranslateViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf


val appModule = module {

    singleOf(::Api) { bind<Api>() }

    singleOf(::LocalStorage) { bind<LocalStorage>() }
    singleOf(::AuthServiceImpl) { bind<AuthService>() }

    singleOf(::SystemMessagesService) { bind() }

    viewModelOf(::AppViewModel)
    viewModelOf(::MainViewModelImpl) { bind<MainViewModel>() }
    viewModelOf(::LoginViewModel)
    viewModelOf(::AddChatViewModel)
    viewModelOf(::ChatViewModelImpl) { bind<ChatViewModel>()}
    viewModelOf(::SystemMessagesViewModelImpl) { bind<SystemMessagesViewModel>() }
    viewModelOf(::SMDetailViewModelImpl) { bind<SMDetailViewModel>() }
    viewModelOf(::FileDialogViewModelImpl) { bind<FileDialogViewModel>() }
    viewModelOf(::TranslateViewModel) { bind() }

    factory { getPlatform() }
}

val appModulePreview = module {

    singleOf(::Api) { bind<Api>() }

    singleOf(::LocalStorage) { bind<LocalStorage>() }
    singleOf(::AuthServiceImpl) { bind<AuthService>() }

    singleOf(::SystemMessagesService) { bind() }

    viewModelOf(::AppViewModel)
    viewModelOf(::MainViewModelPreview) { bind<MainViewModel>() }
    viewModelOf(::LoginViewModel)
    viewModelOf(::AddChatViewModel)
    viewModelOf(::ChatViewModelPreview) { bind<ChatViewModel>()}
    viewModelOf(::SystemMessagesViewModelPreview) { bind<SystemMessagesViewModel>() }
    viewModelOf(::SMDetailViewModelPreview) { bind<SMDetailViewModel>() }
    viewModelOf(::FileDialogViewModelPreview) { bind<FileDialogViewModel>() }
    viewModelOf(::TranslateViewModel) { bind() }

    factory { getPlatform() }
}