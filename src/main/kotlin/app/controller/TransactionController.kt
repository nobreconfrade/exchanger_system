package app.controller

import app.InputFormat
import app.model.TransactionTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class TransactionController {
    fun calculateConvertion(user_data: InputFormat, rates: HashMap<String, Float>): HashMap<String, Any>{
        val value_dest: Float
        val calc_conversion_rate: Float
        if (user_data.from == "USD"){
            value_dest = user_data.value * rates[user_data.to]!! //TODO: Check why (!!) are necessary
            calc_conversion_rate = rates[user_data.to]!!
        } else {
            var value_in_usd = user_data.value / rates[user_data.from]!!
            value_dest = value_in_usd * rates[user_data.to]!!
            calc_conversion_rate = value_dest / user_data.value
        }
        val datetime_now = LocalDateTime.now()
        var transaction_id = transaction {
            TransactionTable.insertAndGetId {
                it[user_key] = user_data.key
                it[currency_orig] = user_data.from
                it[value_orig] = user_data.value
                it[currency_dest] = user_data.to
                it[conversion_rate] = calc_conversion_rate
                it[date] = datetime_now
            }
        }
        return hashMapOf<String, Any>(
            "id" to transaction_id.value,
            "user_key" to user_data.key,
            "currency_orig" to user_data.from,
            "value_orig" to user_data.value,
            "currency_dest" to user_data.to,
            "value_dest" to value_dest,
            "convertion_rate" to calc_conversion_rate,
            "date" to datetime_now.toString()
        )
    }

    fun getTransactionByKey(key: String): MutableList<HashMap<String, Any>?>{
        val trs_by_key = transaction {
            TransactionTable.select{ TransactionTable.user_key eq key}.toList()
        }
        if (trs_by_key.size == 0){
            return mutableListOf()
        } else {
            var result: MutableList<HashMap<String, Any>?> = mutableListOf()
            for (el in trs_by_key) {
                result.add(hashMapOf<String, Any>(
                    "id" to el[TransactionTable.id].value,
                    "user_key" to el[TransactionTable.user_key],
                    "currency_orig" to el[TransactionTable.currency_orig],
                    "value_orig" to el[TransactionTable.value_orig],
                    "currency_dest" to el[TransactionTable.currency_dest],
                    "conversion_rate" to el[TransactionTable.conversion_rate],
                    "date" to el[TransactionTable.date].toString()
                ))
            }
            return result
        }
    }
}