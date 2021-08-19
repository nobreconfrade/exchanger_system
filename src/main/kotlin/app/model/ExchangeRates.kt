package app.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object ExchangeRatesTable: IntIdTable("exchange_rates"){
    val datetime = datetime("datetime")
    val BRL = float("brazilian_real")
    val JPY = float("japanese_yen")
    val EUR = float("european_euro")
    val USD = float("us_dollar")
}

class ExchangeRatesEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExchangeRatesEntity>(ExchangeRatesTable)

    var datetime by ExchangeRatesTable.datetime
    var BRL by ExchangeRatesTable.BRL
    var JPY by ExchangeRatesTable.JPY
    var EUR by ExchangeRatesTable.EUR
    var USD by ExchangeRatesTable.USD

    override fun toString(): String = "ExchangeRates($datetime, $BRL, $JPY, $EUR, $USD)"
}