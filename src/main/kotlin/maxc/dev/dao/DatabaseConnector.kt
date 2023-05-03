package maxc.dev.dao

import org.jetbrains.exposed.sql.Database

class DatabaseConnector(url: String, user: String, password: String) {
    val database = Database.connect(
        url = "jdbc:postgresql://$url",
        //user = user,
        password = password,
        driver = "org.postgresql.Driver"
    )
}