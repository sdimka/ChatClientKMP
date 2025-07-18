package dev.goood.chat_client.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver


class IOSDatabaseDriverFactory: DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "messages.db")
    }
}