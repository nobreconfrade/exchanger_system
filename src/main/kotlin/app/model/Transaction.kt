package app.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object TransactionTable: IntIdTable("transaction"){
    val user_key = varchar("user_key", 250)
    val currency_orig = varchar("currency_origin", 3)
    val value_orig = float("value_origin")
    val currency_dest = varchar("currency_destiny", 3)
    val conversion_rate = float("conversion_rate")
    val date = datetime("date")
}

class TransactionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TransactionEntity>(TransactionTable)

    var user_key by TransactionTable.user_key
    var currency_orig by TransactionTable.currency_orig
    var value_orig by TransactionTable.value_orig
    var currency_dest by TransactionTable.currency_dest
    var conversion_rate by TransactionTable.conversion_rate
    var date by TransactionTable.date

    override fun toString(): String = "TransactionEntity(" +
            "$user_key, " +
            "$currency_orig, " +
            "$value_orig, " +
            "$currency_dest, " +
            "$conversion_rate" +
            "$date)"
}
