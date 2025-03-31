package dev.goood.chat_client.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.includes

//fun initKoin(config : KoinAppDeclaration? = null)
//: KoinApplication {
//    return startKoin {
//        printLogger()
//        includes(config)
//        modules(appModule)
//    }
//}

fun createKoinConfiguration(): KoinConfiguration {
    return KoinConfiguration() {
        modules(appModule)
    }
}