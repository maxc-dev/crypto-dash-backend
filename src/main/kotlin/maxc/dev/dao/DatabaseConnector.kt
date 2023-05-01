package maxc.dev.dao

import org.ktorm.database.Database

class DatabaseConnector(url: String, user: String, password: String) {
    val database = Database.connect(
        url = "jdbc:postgresql://$url",
        user = user,
        password = password,
        driver = "org.postgresql.Driver"
    )
}