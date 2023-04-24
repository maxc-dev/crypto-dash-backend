package maxc.dev.db

import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object AssetTable : Table<Nothing>("assets") {
    val name = varchar("name").primaryKey()
    val price = double("price")
    val timestamp = long("timestamp")
}