package dev.goood.chat_client

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

