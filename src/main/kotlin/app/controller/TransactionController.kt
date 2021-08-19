package app.controller

import app.InputFormat
import java.time.LocalDateTime

class TransactionController {
    fun calculateConvertion(userData: InputFormat, rates: HashMap<String, Float>): HashMap<String, Any>{
        val value_dest: Float
        val convertion_rate: Float
        if (userData.from == "USD"){
            value_dest = userData.value * rates[userData.to]!! //TODO: Check why (!!) are necessary
            convertion_rate = rates[userData.to]!!
        } else {
            var value_in_usd = userData.value / rates[userData.from]!!
            value_dest = value_in_usd * rates[userData.to]!!
            convertion_rate = value_dest / userData.value
        }

        return hashMapOf<String, Any>(
            "id_transaction" to 1, //TODO: Incremental last ID
            "id_user" to userData.id,
            "currency_orig" to userData.from,
            "value_orig" to userData.value,
            "currency_dest" to userData.to,
            "value_dest" to value_dest,
            "convertion_rate" to convertion_rate,
            "date" to LocalDateTime.now().toString()
        )

    }
}