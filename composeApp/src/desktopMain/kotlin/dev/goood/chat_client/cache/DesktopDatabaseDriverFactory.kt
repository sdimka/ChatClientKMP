package dev.goood.chat_client.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import java.util.Properties


class DesktopDatabaseDriverFactory: DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        // JdbcSqliteDriver.IN_MEMORY  "jdbc:sqlite:messages.db"  jdbc:sqlite:test.db

        val dbFile =
            File(System.getProperty("user.home")).resolve("messages.db")

        if (!dbFile.exists()) {
            dbFile.createNewFile()
        }

        return try {
            JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}", Properties(), AppDatabase.Schema)
        } catch (e: Exception) {
            System.err.println("Error creating database driver: ${e.message}")
            e.printStackTrace()
            throw IllegalStateException("Failed to create and initialize the SQLite driver for the desktop application.", e)
        }
    }
}