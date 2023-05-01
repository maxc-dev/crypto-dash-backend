package maxc.dev.dao

import org.jetbrains.exposed.sql.Table


object AssetTable : Table("assets") {
    val name = varchar("name", 12)
    val price = double("price")
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(name)
}