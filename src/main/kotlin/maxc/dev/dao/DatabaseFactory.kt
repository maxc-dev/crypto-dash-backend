package maxc.dev.dao

import io.ktor.server.config.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        // TODO: use env variables 
        // val driverClassName = config.property("storage.driverClassName").getString()
        // val jdbcURL = config.property("storage.jdbcURL").getString()
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://db:5432/ktorjournal?user=postgres"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            // SchemaUtils.create(Articles)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}