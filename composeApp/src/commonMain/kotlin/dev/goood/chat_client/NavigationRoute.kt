package dev.goood.chat_client

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoute {

    @Serializable
    data object AuthGraph: NavigationRoute()

    @Serializable
    data object LoginRoute: NavigationRoute()

    @Serializable
    data object RegisterRoute: NavigationRoute()

    @Serializable
    data object MainGraph: NavigationRoute()

    @Serializable
    data object ChatListRoute: NavigationRoute()

    @Serializable
    data object ChatDetailRoute: NavigationRoute()

    @Serializable
    data object SystemMessagesRoute: NavigationRoute()

    @Serializable
    data object SystemMessageDetailRoute: NavigationRoute()

    @Serializable
    data object SettingsRoute: NavigationRoute()

}