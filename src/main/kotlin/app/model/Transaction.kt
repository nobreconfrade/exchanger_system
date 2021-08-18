package app.model

import java.util.*

data class Transaction(
    val id_transaction: Int,
    val id_user: String,
    val currency_orig: String,
    val value_orig: Float,
    val currency_dest: String,
    val covertion_rate: Float,
    val date: Date
    )
